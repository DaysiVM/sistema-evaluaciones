import { clienteEvaluacion } from '../cliente';
import type { ClienteEvaluacion } from '../tipos';
import { usePeticion } from './usePeticion';

/** E19: confirma que el evaluado existe y que el usuario puede verlo. */
export function useBuscarSujeto(cliente: ClienteEvaluacion = clienteEvaluacion) {
  return usePeticion(cliente.obtenerSujeto);
}
