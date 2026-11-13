import { useCallback, useEffect } from 'react';
import { clienteEvaluacion } from '../cliente';
import type { ClienteEvaluacion, PeriodoResponse, TestDetalleResponse } from '../tipos';
import { usePeticion } from './usePeticion';

export interface OpcionesInicio {
  test: TestDetalleResponse;
  periodos: PeriodoResponse[];
}

/** Carga E2 (configuraciones del test) y E21 (períodos disponibles) al abrir la pantalla. */
export function useOpcionesInicio(testId: number, cliente: ClienteEvaluacion = clienteEvaluacion) {
  const cargar = useCallback(async (): Promise<OpcionesInicio> => {
    const [test, periodos] = await Promise.all([
      cliente.obtenerTest(testId),
      cliente.listarPeriodosDisponibles(),
    ]);
    return { test, periodos };
  }, [cliente, testId]);

  const { estado, ejecutar } = usePeticion(cargar);

  useEffect(() => {
    void ejecutar();
  }, [ejecutar]);

  return { estado, reintentar: ejecutar };
}
