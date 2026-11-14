import { http } from './http';

/**
 * Contratos E5, E6 y E7 de DOC-05 (Documento BFA §5.3) para aplicar una subprueba.
 * Son comunes a todas las subpruebas: no contienen reglas de ningún test ni claves de corrección.
 */

/** Estados de una subprueba dentro de una evaluación (ENT-COM-06). */
export type EstadoSubprueba = 'PENDIENTE' | 'ACTIVA' | 'CERRADA';

/** E5 · 200 SubpruebaActivaResponse. */
export interface SubpruebaActivaResponse {
  codigo: string;
  estado: EstadoSubprueba;
  inicio: string;
  finLimite: string;
  segundosRestantes: number;
  /** Solo aplica a FV. */
  letraInicial?: string;
}

export interface OpcionPregunta {
  letra: string;
  texto: string;
  recursoUrl?: string;
}

export interface PreguntaSubprueba {
  id: number;
  numero: number;
  enunciado: string;
  recursoUrl?: string;
  opciones: OpcionPregunta[];
  /** Letra ya marcada por la persona; `null` si aún no responde. Nunca es la clave correcta. */
  respuestaActual: string | null;
}

/** E6 · 200 PreguntasSubpruebaResponse. */
export interface PreguntasSubpruebaResponse {
  codigo: string;
  segundosRestantes: number;
  preguntas: PreguntaSubprueba[];
}

export interface RespuestaSeleccion {
  preguntaId: number;
  opcion: string;
}

/** E7 · RegistrarRespuestasRequest para subpruebas de selección. */
export interface RegistrarRespuestasRequest {
  respuestas: RespuestaSeleccion[];
}

/** E7 · 200 AvanceSubpruebaResponse. */
export interface AvanceSubpruebaResponse {
  codigo: string;
  registradas: number;
  totalRespondidas: number;
  segundosRestantes: number;
}

/** Puerto usado por las pantallas; el servicio real y el mock lo implementan. */
export interface ClienteSubpruebas {
  iniciar(evaluacionId: number, codigo: string): Promise<SubpruebaActivaResponse>;
  obtenerPreguntas(evaluacionId: number, codigo: string): Promise<PreguntasSubpruebaResponse>;
  registrarRespuestas(
    evaluacionId: number,
    codigo: string,
    respuestas: RespuestaSeleccion[],
  ): Promise<AvanceSubpruebaResponse>;
}

/** Códigos de error de E5–E7 que la interfaz trata de forma específica. */
export const ERRORES_SUBPRUEBA = {
  noActiva: 'SUBPRUEBA_NO_ACTIVA',
  tiempoAgotado: 'TIEMPO_AGOTADO',
  cerrada: 'SUBPRUEBA_CERRADA',
  fueraDeOrden: 'SUBPRUEBA_FUERA_DE_ORDEN',
  hayActiva: 'HAY_SUBPRUEBA_ACTIVA',
  respuestaInvalida: 'RESPUESTA_INVALIDA',
} as const;

const ruta = (evaluacionId: number, codigo: string, accion: string) =>
  `/evaluaciones/${evaluacionId}/subpruebas/${encodeURIComponent(codigo)}/${accion}`;

/** Servicio real sobre `services/http`. */
export function crearClienteSubpruebas(cliente: typeof http = http): ClienteSubpruebas {
  return {
    iniciar: (evaluacionId, codigo) =>
      cliente<SubpruebaActivaResponse>(ruta(evaluacionId, codigo, 'iniciar'), { method: 'POST' }),
    obtenerPreguntas: (evaluacionId, codigo) =>
      cliente<PreguntasSubpruebaResponse>(ruta(evaluacionId, codigo, 'preguntas')),
    registrarRespuestas: (evaluacionId, codigo, respuestas) =>
      cliente<AvanceSubpruebaResponse>(ruta(evaluacionId, codigo, 'respuestas'), {
        method: 'PUT',
        body: JSON.stringify({ respuestas } satisfies RegistrarRespuestasRequest),
      }),
  };
}
