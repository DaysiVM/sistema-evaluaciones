import { clienteEvaluacion } from '../cliente';
import type { ClienteEvaluacion } from '../tipos';
import { usePeticion } from './usePeticion';

/** E3: la evaluación solo existe en la interfaz cuando el servidor responde 201. */
export function useIniciarEvaluacion(cliente: ClienteEvaluacion = clienteEvaluacion) {
  return usePeticion(cliente.iniciarEvaluacion);
}
