import { createBrowserRouter } from 'react-router';
import { RouterProvider } from 'react-router/dom';
import { Plantilla } from './components/Plantilla';
import { BfaFe06Preview } from './dev/BfaFe06Preview';
import { Ui01Preview } from './dev/Ui01Preview';
import { Ui04Preview } from './dev/Ui04Preview';
import { rutasAcceso } from './features/acceso/rutas';
import { rutasBfa } from './features/bfa/rutas';
import { rutasCatalogo } from './features/catalog/rutas';
import { rutasEvaluacion } from './features/evaluacion/rutas';
import { rutasResultados } from './features/resultados/rutas';

// Cada módulo declara sus rutas en features/<modulo>/rutas.tsx; este archivo solo las reúne.
const router = createBrowserRouter([
  {
    element: <Plantilla />,
    children: [
      ...rutasCatalogo,
      ...rutasEvaluacion,
      ...rutasResultados,
      ...rutasAcceso,
      ...rutasBfa,
    ],
  },
  // Vista previa de UI-01 (contenido demostrativo).
  { path: 'dev/ui-01', element: <Ui01Preview /> },
  // Vista previa de UI-04: variantes visuales de las subpruebas BFA (contenido de ejemplo).
  { path: 'dev/ui-04', element: <Ui04Preview /> },
  // Vista previa de BFA-FE-06: familia Numérico con el mock de E5/E6/E7.
  { path: 'dev/bfa-fe-06', element: <BfaFe06Preview /> },
]);

export function App() {
  return <RouterProvider router={router} />;
}
