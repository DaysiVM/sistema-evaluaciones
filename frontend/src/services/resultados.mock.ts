import type {
  ClienteResultados,
  ResultadoFactor,
  ResultadoResponse,
} from '../features/resultados/tipos';
import { ApiError } from './http';

/**
 * Simulación de E9 para desarrollar sin backend. Cumple el mismo contrato que
 * services/resultados.ts.
 *
 * Los valores numéricos son FICTICIOS y solo sirven para probar la interfaz: no son
 * conversiones verificadas de ningún baremo (Documento BFA, reglas de E9).
 *
 * Escenarios por ID de evaluación:
 *   16  → DISPONIBLE: aplicación parcial con factores calculados, sin percentil y no calculables
 *   17  → PENDIENTE_VALIDACION sin revisión publicada (factores vacío)
 *   18  → CALCULANDO sin revisión publicada
 *   15  → 409 EVALUACION_NO_FINALIZADA (en curso, como en el mock de E4)
 *   403 / 500 / 503 → SIN_PERMISO / ERROR_INTERNO / fallo de conexión
 *   Cualquier otro → 404 EVALUACION_NO_ENCONTRADA
 */

const factor = (
  codigo: string,
  datos: Partial<Omit<ResultadoFactor, 'codigo'>>,
): ResultadoFactor => ({
  codigo,
  puntuacionDirecta: null,
  estadoCalculo: 'NO_CALCULABLE',
  percentil: null,
  idRango: null,
  estadoNormativo: 'NO_APLICA',
  motivo: null,
  ...datos,
});

const convertido = (
  codigo: string,
  puntuacionDirecta: number,
  percentil: number,
  idRango: number,
) =>
  factor(codigo, {
    puntuacionDirecta,
    estadoCalculo: 'CALCULADO',
    percentil,
    idRango,
    estadoNormativo: 'CONVERTIDO',
  });

function resultadosIniciales(): ResultadoResponse[] {
  return [
    {
      evaluacionId: 16,
      resultadoId: 7,
      estadoResultado: 'DISPONIBLE',
      versionFormularioId: 10,
      calificacionId: 31,
      revision: 1,
      versionReglas: 'BFA-REGLAS-v1',
      idClave: 4,
      idBaremo: 2,
      factores: [
        convertido('VOC1', 20, 50, 101),
        convertido('VOC2', 18, 45, 102),
        convertido('VOCT', 38, 50, 103),
        factor('R1', {
          puntuacionDirecta: 12,
          estadoCalculo: 'CALCULADO',
          estadoNormativo: 'SIN_COINCIDENCIA',
          motivo: 'La puntuación no coincide con un rango exacto del baremo.',
        }),
        factor('CV', {
          puntuacionDirecta: 0,
          estadoCalculo: 'CALCULADO',
          estadoNormativo: 'SIN_BAREMO_VALIDADO',
          motivo: 'No hay baremo aprobado compatible con la edición aplicada.',
        }),
        factor('FV', { motivo: 'Subprueba no incluida en la aplicación parcial.' }),
        factor('EIV', { motivo: 'Falta un componente: FV.' }),
      ],
    },
    {
      evaluacionId: 17,
      resultadoId: 8,
      estadoResultado: 'PENDIENTE_VALIDACION',
      versionFormularioId: 10,
      calificacionId: null,
      revision: null,
      versionReglas: null,
      idClave: null,
      idBaremo: null,
      factores: [],
    },
    {
      evaluacionId: 18,
      resultadoId: 9,
      estadoResultado: 'CALCULANDO',
      versionFormularioId: 10,
      calificacionId: null,
      revision: null,
      versionReglas: null,
      idClave: null,
      idBaremo: null,
      factores: [],
    },
  ];
}

function fallar(status: number, codigo: string, mensaje: string, ruta: string): never {
  throw new ApiError(status, {
    timestamp: new Date().toISOString(),
    status,
    codigo,
    mensaje,
    ruta,
  });
}

export interface OpcionesMockResultados {
  /** Retardo artificial en ms (0 en pruebas). */
  retardoMs?: number;
}

export function crearClienteResultadosMock({
  retardoMs = 700,
}: OpcionesMockResultados = {}): ClienteResultados {
  const resultados = new Map(resultadosIniciales().map((r) => [r.evaluacionId, r]));
  const esperar = () => new Promise<void>((resolver) => setTimeout(resolver, retardoMs));

  return {
    async obtenerResultado(evaluacionId) {
      await esperar();
      const ruta = `/api/v1/evaluaciones/${evaluacionId}/resultados`;
      if (evaluacionId === 503) throw new TypeError('Failed to fetch');
      if (evaluacionId === 403)
        fallar(403, 'SIN_PERMISO', 'No tiene permiso para consultar estos resultados.', ruta);
      if (evaluacionId === 500) fallar(500, 'ERROR_INTERNO', 'Ocurrió un error inesperado.', ruta);
      if (evaluacionId === 15)
        fallar(409, 'EVALUACION_NO_FINALIZADA', 'La evaluación aún no ha finalizado.', ruta);
      const resultado = resultados.get(evaluacionId);
      if (!resultado)
        fallar(404, 'EVALUACION_NO_ENCONTRADA', 'No existe una evaluación con ese ID.', ruta);
      return structuredClone(resultado);
    },
  };
}
