import type { ClienteResultados, ResultadoResponse } from '../features/resultados/tipos';
import { http } from './http';

/**
 * Llamada real de GEN-05 a /api/v1 (DOC-05). Se usa con VITE_USAR_MOCKS=false.
 * Los errores llegan como ApiError (services/http.ts).
 */
export const servicioResultados: ClienteResultados = {
  /** E9 — contenedor de resultados y revisión vigente exacta (solo usuarios autorizados). */
  obtenerResultado: (evaluacionId) =>
    http<ResultadoResponse>(`/evaluaciones/${evaluacionId}/resultados`),
};
