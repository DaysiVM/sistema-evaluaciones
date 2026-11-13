import type {
  ClienteEvaluacion,
  EvaluacionResponse,
  IniciarEvaluacionRequest,
  PeriodoResponse,
  SujetoResponse,
  TestDetalleResponse,
} from '../features/evaluacion/tipos';
import { http } from './http';

/**
 * Llamadas reales de GEN-02 a /api/v1 (DOC-05). Se usan cuando GEN-02-BE esté integrado
 * y VITE_USAR_MOCKS=false. Los errores llegan como ApiError (services/http.ts).
 */
export const servicioEvaluacion: ClienteEvaluacion = {
  /** E2 — configuraciones de la edición publicada. */
  obtenerTest: (testId) => http<TestDetalleResponse>(`/tests/${testId}`),

  /** E21 — solo períodos que admiten nuevas asignaciones. */
  listarPeriodosDisponibles: () => http<PeriodoResponse[]>('/periodos?disponible=true'),

  /** E19 — datos autorizados del evaluado. */
  obtenerSujeto: (sujetoId) => http<SujetoResponse>(`/sujetos/${sujetoId}`),

  /** E3 — crea la evaluación EN_CURSO; responde 201 con la misma forma que E4. */
  iniciarEvaluacion: (solicitud: IniciarEvaluacionRequest) =>
    http<EvaluacionResponse>('/evaluaciones', {
      method: 'POST',
      body: JSON.stringify(solicitud),
    }),

  /** E4 — estado actual para retomar sin reiniciar el reloj. */
  obtenerEvaluacion: (evaluacionId) => http<EvaluacionResponse>(`/evaluaciones/${evaluacionId}`),
};
