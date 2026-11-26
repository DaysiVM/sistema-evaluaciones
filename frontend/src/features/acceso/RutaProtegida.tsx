import { Navigate, Outlet } from 'react-router';
import { useSesion } from './sesion';
import type { Rol } from './tipos';

/** Restringe la navegación; la API sigue validando permisos y alcance de cada operación. */
export function RutaProtegida({ roles }: { roles?: readonly Rol[] }) {
  const { sesion } = useSesion();
  if (!sesion) return <Navigate to="/acceso" replace />;
  if (roles && !roles.includes(sesion.rolActivo)) {
    return <p role="alert">No tiene permiso para acceder a esta página.</p>;
  }
  return <Outlet />;
}
