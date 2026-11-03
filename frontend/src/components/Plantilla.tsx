import { Outlet } from 'react-router';

/** Marco común de todas las páginas. */
export function Plantilla() {
  return (
    <main className="app">
      <Outlet />
    </main>
  );
}
