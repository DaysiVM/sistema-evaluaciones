import { afterEach, describe, expect, it, vi } from 'vitest';
import { clienteAcceso } from './cliente';
import { ApiError, establecerToken } from '../../services/http';

afterEach(() => {
  establecerToken(null);
  vi.unstubAllGlobals();
});

/** Cuerpo de error común del backend con datos ficticios. */
function errorApi(status: number, codigo: string, ruta: string) {
  return Response.json(
    {
      timestamp: '2099-01-01T00:00:00Z',
      status,
      codigo,
      mensaje: 'Mensaje ficticio',
      ruta,
    },
    { status },
  );
}

describe('cliente de acceso', () => {
  it('E13 envía el registro y devuelve la cuenta creada (201)', async () => {
    const solicitud = {
      correo: 'prueba@example.invalid',
      contrasena: 'entrada-ficticia-123',
      nombres: 'Nombre',
      apellidos: 'Ficticio',
      rol: 'SUJETO' as const,
    };
    const cuenta = { id: 7, correo: solicitud.correo, rol: 'SUJETO', estado: 'ACTIVA' };
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(Response.json(cuenta, { status: 201 })));
    await expect(clienteAcceso.registrar(solicitud)).resolves.toEqual(cuenta);
    expect(fetch).toHaveBeenCalledWith(
      '/api/v1/auth/registro',
      expect.objectContaining({ method: 'POST', body: JSON.stringify(solicitud) }),
    );
  });

  it.each([
    [409, 'CUENTA_EXISTENTE'],
    [403, 'ROL_NO_AUTORIZADO'],
  ])('E13 rechaza con %i %s según el contrato', async (status, codigo) => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(errorApi(status, codigo, '/api/v1/auth/registro')),
    );
    const promesa = clienteAcceso.registrar({
      correo: 'prueba@example.invalid',
      contrasena: 'entrada-ficticia-123',
      nombres: 'Nombre',
      apellidos: 'Ficticio',
      rol: 'PSICOLOGO',
    });
    await expect(promesa).rejects.toBeInstanceOf(ApiError);
    await expect(promesa).rejects.toMatchObject({ status, error: { codigo } });
  });

  it('E14 rechaza credenciales inválidas (401) sin cerrar la sesión de la app', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(errorApi(401, 'CREDENCIALES_INVALIDAS', '/api/v1/auth/sesion')),
    );
    await expect(
      clienteAcceso.iniciarSesion({
        correo: 'prueba@example.invalid',
        contrasena: 'entrada-ficticia',
      }),
    ).rejects.toMatchObject({ status: 401, error: { codigo: 'CREDENCIALES_INVALIDAS' } });
  });

  it('E16 pide la recuperación y acepta el 202 sin cuerpo', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(null, { status: 202 })));
    await expect(
      clienteAcceso.pedirRecuperacion({ correo: 'prueba@example.invalid' }),
    ).resolves.toBeUndefined();
    expect(fetch).toHaveBeenCalledWith(
      '/api/v1/auth/recuperacion',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ correo: 'prueba@example.invalid' }),
      }),
    );
  });

  it('E16 informa CORREO_NO_DISPONIBLE (503)', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(errorApi(503, 'CORREO_NO_DISPONIBLE', '/api/v1/auth/recuperacion')),
    );
    await expect(
      clienteAcceso.pedirRecuperacion({ correo: 'prueba@example.invalid' }),
    ).rejects.toMatchObject({ status: 503, error: { codigo: 'CORREO_NO_DISPONIBLE' } });
  });

  it('E17 confirma la nueva contraseña con el token y acepta el 204', async () => {
    const solicitud = { token: 'token-ficticio', nuevaContrasena: 'nueva-ficticia-123' };
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(null, { status: 204 })));
    await expect(clienteAcceso.confirmarRecuperacion(solicitud)).resolves.toBeUndefined();
    expect(fetch).toHaveBeenCalledWith(
      '/api/v1/auth/recuperacion/confirmar',
      expect.objectContaining({ method: 'POST', body: JSON.stringify(solicitud) }),
    );
  });

  it('E17 rechaza un token vencido o usado (400 TOKEN_INVALIDO)', async () => {
    vi.stubGlobal(
      'fetch',
      vi
        .fn()
        .mockResolvedValue(errorApi(400, 'TOKEN_INVALIDO', '/api/v1/auth/recuperacion/confirmar')),
    );
    await expect(
      clienteAcceso.confirmarRecuperacion({
        token: 'token-ficticio',
        nuevaContrasena: 'nueva-ficticia-123',
      }),
    ).rejects.toMatchObject({ status: 400, error: { codigo: 'TOKEN_INVALIDO' } });
  });

  it('E14 envía correo y contraseña y devuelve el token opaco con rol y permisos', async () => {
    const sesion = {
      token: 'token-ficticio',
      expiraEn: '2099-01-01T00:00:00Z',
      rolActivo: 'SUJETO',
      permisos: ['PRACTICA_REALIZAR'],
    };
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(Response.json(sesion)));
    await expect(
      clienteAcceso.iniciarSesion({
        correo: 'prueba@example.invalid',
        contrasena: 'entrada-ficticia',
      }),
    ).resolves.toEqual(sesion);
    expect(fetch).toHaveBeenCalledWith(
      '/api/v1/auth/sesion',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({ correo: 'prueba@example.invalid', contrasena: 'entrada-ficticia' }),
      }),
    );
  });

  it('E15 revoca el Bearer actual y acepta la respuesta vacía', async () => {
    establecerToken('token-ficticio');
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(null, { status: 204 })));
    await expect(clienteAcceso.cerrarSesion()).resolves.toBeUndefined();
    expect(fetch).toHaveBeenCalledWith(
      '/api/v1/auth/sesion',
      expect.objectContaining({ method: 'DELETE' }),
    );
    expect(new Headers(vi.mocked(fetch).mock.calls[0][1]?.headers).get('Authorization')).toBe(
      'Bearer token-ficticio',
    );
  });
});
