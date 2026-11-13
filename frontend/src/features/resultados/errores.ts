import { aErrorMostrable } from '../evaluacion/mensajesError';
import type { ErrorMostrable } from '../evaluacion/tipos';

/**
 * Errores de E9 sobre el traductor común de GEN-02 (Documento general §5.2.2):
 * 403 SIN_PERMISO, 404 EVALUACION_NO_ENCONTRADA y 409 EVALUACION_NO_FINALIZADA.
 */
const MENSAJES_E9: Record<string, string> = {
  SIN_PERMISO: 'No tiene permiso para consultar los resultados de esta evaluación.',
  EVALUACION_NO_FINALIZADA:
    'La evaluación aún no ha finalizado. Los resultados se podrán consultar cuando se finalice.',
};

export function aErrorResultados(error: unknown): ErrorMostrable {
  const base = aErrorMostrable(error);
  const mensaje = MENSAJES_E9[base.codigo];
  return mensaje ? { ...base, mensaje } : base;
}

/** La evaluación existe pero no está finalizada: se presenta como aviso, no como fallo. */
export const esNoFinalizada = (error: ErrorMostrable) =>
  error.codigo === 'EVALUACION_NO_FINALIZADA';
