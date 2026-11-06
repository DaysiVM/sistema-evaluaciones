import { clienteEvaluacion } from '../cliente';
import type { ClienteEvaluacion } from '../tipos';
import { usePeticion } from './usePeticion';

/** E4: recupera estado y progreso sin crear otro intento ni reiniciar el reloj. */
export function useRetomarEvaluacion(cliente: ClienteEvaluacion = clienteEvaluacion) {
  return usePeticion(cliente.obtenerEvaluacion);
}
