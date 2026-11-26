import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { guardarSesion, leerSesionGuardada } from './sesion';
import type { Sesion } from './tipos';

const sesion: Sesion = {
  token: 'token-ficticio',
  correo: 'prueba@example.invalid',
  expiraEn: '2099-01-01T00:00:00Z',
  rolActivo: 'SUJETO',
  permisos: ['PRACTICA_REALIZAR'],
};

beforeEach(() => {
  const datos = new Map<string, string>();
  vi.stubGlobal('sessionStorage', {
    getItem: (clave: string) => datos.get(clave) ?? null,
    setItem: (clave: string, valor: string) => datos.set(clave, valor),
    removeItem: (clave: string) => datos.delete(clave),
  });
});
afterEach(() => vi.unstubAllGlobals());

describe('sesión guardada', () => {
  it('restaura la sesión vigente y permite cerrarla', () => {
    guardarSesion(sesion);
    expect(leerSesionGuardada()).toEqual(sesion);
    guardarSesion(null);
    expect(leerSesionGuardada()).toBeNull();
  });

  it.each([
    { ...sesion, expiraEn: '2000-01-01T00:00:00Z' },
    { ...sesion, expiraEn: 'fecha-invalida' },
    { ...sesion, token: '' },
    { ...sesion, rolActivo: 'ADMIN' },
    { ...sesion, permisos: null },
  ])('descarta una sesión vencida o dañada: %j', (invalida) => {
    sessionStorage.setItem('sesion-evaluaciones', JSON.stringify(invalida));
    expect(leerSesionGuardada()).toBeNull();
    expect(sessionStorage.getItem('sesion-evaluaciones')).toBeNull();
  });

  it('ignora JSON corrupto y almacenamiento no disponible', () => {
    sessionStorage.setItem('sesion-evaluaciones', '{');
    expect(leerSesionGuardada()).toBeNull();
    vi.stubGlobal('sessionStorage', undefined);
    expect(leerSesionGuardada()).toBeNull();
    expect(() => guardarSesion(sesion)).not.toThrow();
  });
});
