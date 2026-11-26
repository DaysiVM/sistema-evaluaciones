import { renderToStaticMarkup } from 'react-dom/server';
import { MemoryRouter, Route, Routes } from 'react-router';
import { describe, expect, it, vi } from 'vitest';
import { RutaProtegida } from './RutaProtegida';
import { SesionContexto } from './sesion';
import { ROLES, type Rol, type Sesion } from './tipos';

function pantalla(rol: Rol | null, roles?: readonly Rol[]) {
  const sesion: Sesion | null = rol
    ? {
        token: 'token-ficticio',
        correo: 'prueba@example.invalid',
        expiraEn: '2099-01-01T00:00:00Z',
        rolActivo: rol,
        permisos: [],
      }
    : null;
  return renderToStaticMarkup(
    <SesionContexto value={{ sesion, iniciar: vi.fn(), cerrar: vi.fn() }}>
      <MemoryRouter initialEntries={['/protegida']}>
        <Routes>
          <Route element={<RutaProtegida roles={roles} />}>
            <Route path="protegida" element={<p>Contenido protegido</p>} />
          </Route>
        </Routes>
      </MemoryRouter>
    </SesionContexto>,
  );
}

describe('ruta protegida', () => {
  it('no muestra contenido protegido sin sesión', () => {
    expect(pantalla(null)).not.toContain('Contenido protegido');
  });

  it.each(ROLES)('permite una ruta común al rol %s autenticado', (rol) => {
    expect(pantalla(rol)).toContain('Contenido protegido');
  });

  it('deniega al Sujeto una ruta reservada a profesionales', () => {
    const html = pantalla('SUJETO', ['PSICOLOGO', 'COORDINADOR', 'ACADEMICO', 'DECANO']);
    expect(html).not.toContain('Contenido protegido');
    expect(html).toContain('No tiene permiso');
  });

  it('permite al Coordinador una ruta que lo autoriza', () => {
    expect(pantalla('COORDINADOR', ['COORDINADOR'])).toContain('Contenido protegido');
  });
});
