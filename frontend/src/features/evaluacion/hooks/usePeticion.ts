import { useCallback, useRef, useState } from 'react';
import { aErrorMostrable } from '../mensajesError';
import type { ErrorMostrable, EstadoPeticion } from '../tipos';

/**
 * Ciclo inicial → cargando → (exito | error) para una llamada a la API.
 * Los datos solo se guardan cuando la llamada se confirma; si llega una respuesta
 * vieja después de otra más nueva, se descarta.
 * `traducirError` permite a otro módulo ajustar los mensajes sin duplicar este ciclo.
 */
export function usePeticion<A extends unknown[], T>(
  operacion: (...args: A) => Promise<T>,
  traducirError: (error: unknown) => ErrorMostrable = aErrorMostrable,
) {
  const [estado, setEstado] = useState<EstadoPeticion<T>>({ fase: 'inicial' });
  const ultima = useRef(0);

  const ejecutar = useCallback(
    async (...args: A): Promise<T | null> => {
      const id = ++ultima.current;
      setEstado({ fase: 'cargando' });
      try {
        const datos = await operacion(...args);
        if (id === ultima.current) setEstado({ fase: 'exito', datos });
        return datos;
      } catch (error: unknown) {
        if (id === ultima.current) setEstado({ fase: 'error', error: traducirError(error) });
        return null;
      }
    },
    [operacion, traducirError],
  );

  const reiniciar = useCallback(() => {
    ultima.current++;
    setEstado({ fase: 'inicial' });
  }, []);

  return { estado, ejecutar, reiniciar };
}
