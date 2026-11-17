import { usePeticion } from '../../evaluacion/hooks/usePeticion';
import { clienteResultados } from '../cliente';
import { aErrorResultados } from '../errores';
import type { ClienteResultados } from '../tipos';

/** E9: estados de carga, éxito y error; los datos se muestran tal como llegan. */
export function useResultado(cliente: ClienteResultados = clienteResultados) {
  return usePeticion(cliente.obtenerResultado, aErrorResultados);
}
