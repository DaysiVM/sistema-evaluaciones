import { createBrowserRouter } from 'react-router';
import { RouterProvider } from 'react-router/dom';
import { Plantilla } from './components/Plantilla';
import { Ui01Preview } from './dev/Ui01Preview';
import { Ui04Preview } from './dev/Ui04Preview';
import { Ui05Preview } from './dev/Ui05Preview';
import { SesionProveedor } from './features/acceso/SesionProveedor';
import { RutaProtegida } from './features/acceso/RutaProtegida';
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
      ...rutasAcceso,
      {
        element: <RutaProtegida />,
        // E9 decide el acceso a resultados según la audiencia publicada, incluido SUJETO.
        children: [...rutasCatalogo, ...rutasEvaluacion, ...rutasBfa, ...rutasResultados],
      },
    ],
  },
  // Vista previa de UI-01 (contenido demostrativo).
  { path: 'dev/ui-01', element: <Ui01Preview /> },
  // Vista previa de UI-04: variantes visuales de las subpruebas BFA (contenido de ejemplo).
  { path: 'dev/ui-04', element: <Ui04Preview /> },
  // Vista previa de UI-05: diseño del perfil de resultados BFA (datos simulados).
  { path: 'dev/ui-05', element: <Ui05Preview /> },
]);

const volverAlLogin = () => {
  void router.navigate('/acceso', { replace: true });
};

export function App() {
  return (
    <SesionProveedor alExpirar={volverAlLogin}>
      <RouterProvider router={router} />
    </SesionProveedor>
  );
}
