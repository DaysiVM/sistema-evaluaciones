/// <reference types="vite/client" />
import { servicioEvaluacion } from '../../services/evaluacion';
import { crearClienteMock } from '../../services/evaluacion.mock';
import type { ClienteEvaluacion } from './tipos';

/**
 * Punto único donde se elige la implementación de la API de evaluación.
 * Mientras GEN-02-BE no esté integrado se usa el mock; para usar el backend real,
 * crear frontend/.env.local con VITE_USAR_MOCKS=false.
 */
export const usaMocks = import.meta.env.VITE_USAR_MOCKS !== 'false';

export const clienteEvaluacion: ClienteEvaluacion = usaMocks
  ? crearClienteMock()
  : servicioEvaluacion;
