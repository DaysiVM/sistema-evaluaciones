import type { RouteObject } from 'react-router';
import { ResultadosPagina } from './ResultadosPagina';

export const rutasResultados: RouteObject[] = [
  // GEN-05-FE: buscador por ID y consulta directa (E9).
  { path: 'resultados', element: <ResultadosPagina /> },
  { path: 'evaluaciones/:evaluacionId/resultados', element: <ResultadosPagina /> },
];
