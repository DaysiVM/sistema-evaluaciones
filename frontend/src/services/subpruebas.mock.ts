import { ApiError, type ErrorApi } from './http';
import {
  ERRORES_SUBPRUEBA,
  type AvanceSubpruebaResponse,
  type ClienteSubpruebas,
  type EstadoSubprueba,
  type OpcionPregunta,
  type PreguntaSubprueba,
  type PreguntasSubpruebaResponse,
  type RespuestaSeleccion,
  type SubpruebaActivaResponse,
} from './subpruebas';

/**
 * Simulación de E5, E6 y E7 para la familia Numérico (solo desarrollo).
 * Las preguntas son de EJEMPLO: no son reactivos del BFA y el mock no conoce ninguna clave.
 * Tiempos oficiales: Documento BFA §2.6 (240 s Sumas y Multiplicaciones, 360 s Operaciones y Problemas).
 */

/** Orden oficial de la familia dentro del cuadernillo (Documento BFA §2.6). */
export const ORDEN_NUMERICO = ['N1-A', 'N1-B', 'N2-A', 'N2-B'] as const;

const SEGUNDOS_OFICIALES: Record<string, number> = {
  'N1-A': 240,
  'N1-B': 240,
  'N2-A': 360,
  'N2-B': 360,
};

/** Evaluación de ejemplo cuyo guardado falla por red (para ver «No se pudo guardar»). */
export const EVALUACION_SIN_RED = 503;

const AB = (letras: string[], textos: string[]): OpcionPregunta[] =>
  letras.map((letra, i) => ({ letra, texto: textos[i] }));

const VERDADERO_FALSO = AB(['A', 'B'], ['Verdadero', 'Falso']);

/** Operación binaria de ejemplo con un resultado propuesto que a veces no coincide. */
function operacionesBinarias(base: number, cantidad: number, operador: '+' | '×') {
  return Array.from({ length: cantidad }, (_, i) => {
    const a = operador === '+' ? 100 + ((i * 137) % 900) : 12 + ((i * 7) % 88);
    const b = operador === '+' ? 10 + ((i * 59) % 490) : 2 + ((i * 3) % 9);
    const exacto = operador === '+' ? a + b : a * b;
    const propuesto = i % 3 === 1 ? exacto + (i % 2 === 0 ? 10 : -1) : exacto;
    return {
      id: base + i,
      numero: i + 1,
      enunciado: `${a} ${operador} ${b} = ${propuesto}`,
      opciones: VERDADERO_FALSO,
    };
  });
}

type PreguntaBase = Omit<PreguntaSubprueba, 'respuestaActual'>;

const ABCD = (textos: string[]) => AB(['A', 'B', 'C', 'D'], textos);

const PREGUNTAS: Record<string, PreguntaBase[]> = {
  'N1-A': operacionesBinarias(1000, 40, '+'),
  'N1-B': operacionesBinarias(2000, 30, '×'),
  'N2-A': [
    { id: 3001, numero: 1, enunciado: '45 − ___ = 18', opciones: ABCD(['17', '27', '37', '63']) },
    {
      id: 3002,
      numero: 2,
      enunciado: '2.5 + ___ = 4.75',
      opciones: ABCD(['1.25', '2.25', '2.75', '7.25']),
    },
    {
      id: 3003,
      numero: 3,
      enunciado: '1/2 + ___ = 3/4',
      opciones: ABCD(['1/4', '1/2', '2/3', '3/8']),
    },
    { id: 3004, numero: 4, enunciado: '___ ÷ 6 = 12', opciones: ABCD(['2', '18', '66', '72']) },
    {
      id: 3005,
      numero: 5,
      enunciado: '( 8 × ___ ) − 5 = 27',
      opciones: ABCD(['3', '4', '5', '22']),
    },
    { id: 3006, numero: 6, enunciado: '−3 + ___ = 4', opciones: ABCD(['−7', '1', '7', '12']) },
  ],
  'N2-B': [
    {
      id: 4001,
      numero: 1,
      enunciado:
        'Una tienda recibe 48 cajas con 12 lápices cada una. Si durante la semana vende 150 lápices, ¿cuántos lápices le quedan?',
      opciones: ABCD(['416 lápices', '426 lápices', '436 lápices', '576 lápices']),
    },
    {
      id: 4002,
      numero: 2,
      enunciado:
        'Un depósito contiene 2.5 litros de agua. Se agregan 3/4 de litro y luego se usan 1.25 litros. ¿Cuántos litros quedan?',
      opciones: ABCD(['1.5 litros', '2 litros', '2.25 litros', '3.5 litros']),
    },
    {
      id: 4003,
      numero: 3,
      enunciado: 'Tres personas se reparten C$ 1 350 en partes iguales. ¿Cuánto recibe cada una?',
      opciones: ABCD(['C$ 350', 'C$ 400', 'C$ 450', 'C$ 4 050']),
    },
    {
      id: 4004,
      numero: 4,
      enunciado: 'Un bus sale a las 7:40 y el viaje dura 2 horas y 35 minutos. ¿A qué hora llega?',
      opciones: ABCD(['9:15', '10:05', '10:15', '10:25']),
    },
  ],
};

type Ejecucion = {
  estado: EstadoSubprueba;
  inicio?: number;
  finLimite?: number;
  respuestas: Map<number, string>;
};

export type OpcionesMock = {
  /** Espera simulada por llamada. */
  retardoMs?: number;
  /** Reloj inyectable (ms desde epoch) para pruebas. */
  ahora?: () => number;
  /** Sustituye el tiempo oficial (solo para revisar el cierre en desarrollo). */
  duracionSegundos?: number;
  /** `false` permite iniciar cualquier subprueba de la familia por separado (vista previa). */
  validarOrden?: boolean;
};

function errorApi(status: number, codigo: string, mensaje: string, ruta: string): ApiError {
  const cuerpo: ErrorApi = { timestamp: new Date().toISOString(), status, codigo, mensaje, ruta };
  return new ApiError(status, cuerpo);
}

export function crearClienteSubpruebasMock({
  retardoMs = 300,
  ahora = Date.now,
  duracionSegundos,
  validarOrden = true,
}: OpcionesMock = {}): ClienteSubpruebas {
  const ejecuciones = new Map<string, Ejecucion>();
  const clave = (evaluacionId: number, codigo: string) => `${evaluacionId}:${codigo}`;
  const espera = () =>
    retardoMs > 0 ? new Promise((r) => setTimeout(r, retardoMs)) : Promise.resolve();

  /** Devuelve la ejecución aplicando el cierre por tiempo que haría el servidor. */
  function ejecucion(evaluacionId: number, codigo: string): Ejecucion {
    const k = clave(evaluacionId, codigo);
    let e = ejecuciones.get(k);
    if (!e) {
      e = { estado: 'PENDIENTE', respuestas: new Map() };
      ejecuciones.set(k, e);
    }
    if (e.estado === 'ACTIVA' && e.finLimite !== undefined && ahora() >= e.finLimite) {
      e.estado = 'CERRADA';
    }
    return e;
  }

  const restantes = (e: Ejecucion) => Math.max(0, Math.ceil(((e.finLimite ?? 0) - ahora()) / 1000));

  function validarCodigo(codigo: string, ruta: string) {
    if (!PREGUNTAS[codigo]) {
      throw errorApi(404, 'SUBPRUEBA_NO_ENCONTRADA', 'La subprueba no existe.', ruta);
    }
  }

  return {
    async iniciar(evaluacionId, codigo) {
      await espera();
      const ruta = `/api/v1/evaluaciones/${evaluacionId}/subpruebas/${codigo}/iniciar`;
      validarCodigo(codigo, ruta);
      const e = ejecucion(evaluacionId, codigo);
      if (e.estado === 'CERRADA') {
        throw errorApi(409, ERRORES_SUBPRUEBA.cerrada, 'La subprueba ya está cerrada.', ruta);
      }
      if (e.estado === 'PENDIENTE') {
        const posicion = ORDEN_NUMERICO.indexOf(codigo as (typeof ORDEN_NUMERICO)[number]);
        const otras = ORDEN_NUMERICO.map((c) => ejecucion(evaluacionId, c).estado);
        if (validarOrden && otras.includes('ACTIVA')) {
          throw errorApi(409, ERRORES_SUBPRUEBA.hayActiva, 'Hay otra subprueba activa.', ruta);
        }
        if (validarOrden && otras.slice(0, posicion).some((estado) => estado === 'PENDIENTE')) {
          throw errorApi(
            409,
            ERRORES_SUBPRUEBA.fueraDeOrden,
            'Debe aplicarse primero la subprueba anterior.',
            ruta,
          );
        }
        const inicio = ahora();
        e.estado = 'ACTIVA';
        e.inicio = inicio;
        e.finLimite = inicio + (duracionSegundos ?? SEGUNDOS_OFICIALES[codigo]) * 1000;
      }
      // Repetir E5 sobre la misma subprueba activa devuelve el mismo inicio (CU-BFA-P03).
      const respuesta: SubpruebaActivaResponse = {
        codigo,
        estado: e.estado,
        inicio: new Date(e.inicio ?? 0).toISOString(),
        finLimite: new Date(e.finLimite ?? 0).toISOString(),
        segundosRestantes: restantes(e),
      };
      return respuesta;
    },

    async obtenerPreguntas(evaluacionId, codigo) {
      await espera();
      const ruta = `/api/v1/evaluaciones/${evaluacionId}/subpruebas/${codigo}/preguntas`;
      validarCodigo(codigo, ruta);
      const e = ejecucion(evaluacionId, codigo);
      if (e.estado !== 'ACTIVA') {
        throw errorApi(409, ERRORES_SUBPRUEBA.noActiva, 'La subprueba no está activa.', ruta);
      }
      const respuesta: PreguntasSubpruebaResponse = {
        codigo,
        segundosRestantes: restantes(e),
        preguntas: PREGUNTAS[codigo].map((p) => ({
          ...p,
          opciones: p.opciones.map((o) => ({ ...o })),
          respuestaActual: e.respuestas.get(p.id) ?? null,
        })),
      };
      return respuesta;
    },

    async registrarRespuestas(evaluacionId, codigo, respuestas: RespuestaSeleccion[]) {
      await espera();
      const ruta = `/api/v1/evaluaciones/${evaluacionId}/subpruebas/${codigo}/respuestas`;
      if (evaluacionId === EVALUACION_SIN_RED) throw new TypeError('Failed to fetch');
      validarCodigo(codigo, ruta);
      const e = ejecucion(evaluacionId, codigo);
      if (e.estado !== 'ACTIVA') {
        const vencida = e.finLimite !== undefined && ahora() >= e.finLimite;
        throw vencida
          ? errorApi(
              409,
              ERRORES_SUBPRUEBA.tiempoAgotado,
              'El tiempo de la subprueba terminó.',
              ruta,
            )
          : errorApi(409, ERRORES_SUBPRUEBA.noActiva, 'La subprueba no está activa.', ruta);
      }
      for (const r of respuestas) {
        const pregunta = PREGUNTAS[codigo].find((p) => p.id === r.preguntaId);
        if (!pregunta || !pregunta.opciones.some((o) => o.letra === r.opcion)) {
          throw errorApi(
            422,
            ERRORES_SUBPRUEBA.respuestaInvalida,
            'La opción no pertenece a la pregunta.',
            ruta,
          );
        }
      }
      respuestas.forEach((r) => e.respuestas.set(r.preguntaId, r.opcion));
      const avance: AvanceSubpruebaResponse = {
        codigo,
        registradas: respuestas.length,
        totalRespondidas: e.respuestas.size,
        segundosRestantes: restantes(e),
      };
      return avance;
    },
  };
}
