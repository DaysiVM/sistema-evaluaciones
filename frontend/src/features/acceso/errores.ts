import { ApiError } from '../../services/http';
import { aErrorMostrable } from '../evaluacion/mensajesError';
import type { ErrorMostrable } from '../evaluacion/tipos';

/** Mensajes de los códigos propios de E13–E17 (docs/api/openapi.yaml). */
const MENSAJES_ACCESO: Record<string, string> = {
  CORREO_NO_DISPONIBLE:
    'No se pudo enviar el correo de recuperación en este momento. Intente de nuevo más tarde.',
};

/**
 * Traduce los errores de acceso. Un 503 con cuerpo del backend (E16 CORREO_NO_DISPONIBLE) no es
 * una caída de red: el servidor respondió, así que no se muestra «Sin conexión».
 */
export function aErrorAcceso(error: unknown): ErrorMostrable {
  if (error instanceof ApiError && error.error?.codigo) {
    const mensaje = MENSAJES_ACCESO[error.error.codigo];
    if (mensaje)
      return {
        tipo: 'servidor',
        codigo: error.error.codigo,
        mensaje,
        detalles: error.error.detalles ?? [],
      };
  }
  return aErrorMostrable(error);
}
