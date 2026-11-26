import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError, establecerToken, http, alNoAutorizado } from './http';

const responder = (status: number, cuerpo?: unknown) =>
  vi.stubGlobal(
    'fetch',
    vi
      .fn()
      .mockResolvedValue(
        new Response(cuerpo === undefined ? null : JSON.stringify(cuerpo), { status }),
      ),
  );

afterEach(() => {
  establecerToken(null);
  vi.unstubAllGlobals();
});

describe('http', () => {
  it('envía el token opaco de E14 como Bearer y conserva Headers del consumidor', async () => {
    establecerToken('token-opaco-de-prueba');
    responder(200, []);
    await http('/tests', { headers: new Headers({ 'X-Solicitud': 'consulta' }) });
    const headers = new Headers(vi.mocked(fetch).mock.calls[0][1]?.headers);
    expect(headers.get('Authorization')).toBe('Bearer token-opaco-de-prueba');
    expect(headers.get('X-Solicitud')).toBe('consulta');
    expect(headers.get('Content-Type')).toBe('application/json');
  });

  it('sin sesión no envía Authorization y acepta 204 sin JSON', async () => {
    responder(204);
    await expect(http('/auth/sesion', { method: 'DELETE' })).resolves.toBeUndefined();
    expect(new Headers(vi.mocked(fetch).mock.calls[0][1]?.headers).has('Authorization')).toBe(
      false,
    );
  });

  it('ante 401 avisa a la sesión y deja de enviar el token rechazado', async () => {
    establecerToken('token-vencido');
    const expirada = vi.fn();
    const cancelar = alNoAutorizado(expirada);
    try {
      responder(401);
      await expect(http('/tests')).rejects.toMatchObject({ status: 401 });
      expect(expirada).toHaveBeenCalledOnce();
      responder(200, []);
      await http('/tests');
      expect(new Headers(vi.mocked(fetch).mock.calls[0][1]?.headers).has('Authorization')).toBe(
        false,
      );
    } finally {
      cancelar();
    }
  });

  it('un 401 de E14 conserva el error de credenciales sin redirigir', async () => {
    const expirada = vi.fn();
    const cancelar = alNoAutorizado(expirada);
    try {
      responder(401, { codigo: 'CREDENCIALES_INVALIDAS' });
      await expect(http('/auth/sesion', { method: 'POST' })).rejects.toMatchObject({
        status: 401,
        error: { codigo: 'CREDENCIALES_INVALIDAS' },
      });
      expect(expirada).not.toHaveBeenCalled();
    } finally {
      cancelar();
    }
  });

  it('un 401 tardío de la sesión anterior no cierra la nueva', async () => {
    establecerToken('token-anterior');
    let responderAnterior!: (respuesta: Response) => void;
    vi.stubGlobal(
      'fetch',
      vi.fn(
        () =>
          new Promise<Response>((resolver) => {
            responderAnterior = resolver;
          }),
      ),
    );
    const expirada = vi.fn();
    const cancelar = alNoAutorizado(expirada);
    try {
      const anterior = http('/tests');
      establecerToken('token-nuevo');
      responderAnterior(new Response(null, { status: 401 }));
      await expect(anterior).rejects.toMatchObject({ status: 401 });
      expect(expirada).not.toHaveBeenCalled();
      responder(200, []);
      await http('/tests');
      expect(new Headers(vi.mocked(fetch).mock.calls[0][1]?.headers).get('Authorization')).toBe(
        'Bearer token-nuevo',
      );
    } finally {
      cancelar();
    }
  });

  it('un 403 conserva la sesión para mostrar el permiso denegado', async () => {
    establecerToken('token-valido');
    const expirada = vi.fn();
    const cancelar = alNoAutorizado(expirada);
    try {
      responder(403, { codigo: 'SIN_PERMISO' });
      await expect(http('/tests')).rejects.toMatchObject({ status: 403 });
      expect(expirada).not.toHaveBeenCalled();
      responder(200, []);
      await http('/tests');
      expect(new Headers(vi.mocked(fetch).mock.calls[0][1]?.headers).get('Authorization')).toBe(
        'Bearer token-valido',
      );
    } finally {
      cancelar();
    }
  });

  it('llama a /api/v1 y devuelve el JSON', async () => {
    responder(200, [{ id: 1 }]);
    await expect(http('/tests')).resolves.toEqual([{ id: 1 }]);
    expect(fetch).toHaveBeenCalledWith('/api/v1/tests', expect.anything());
  });

  it('convierte el error del backend en ApiError con su código', async () => {
    responder(409, {
      timestamp: 't',
      status: 409,
      codigo: 'EVALUACION_FINALIZADA',
      mensaje: 'Ya finalizó.',
      ruta: '/x',
    });
    await expect(http('/x')).rejects.toMatchObject({
      status: 409,
      error: { codigo: 'EVALUACION_FINALIZADA' },
    });
  });

  it('sin cuerpo de error conserva el estado HTTP', async () => {
    responder(502);
    const fallo = await http('/x').catch((e: unknown) => e);
    expect(fallo).toBeInstanceOf(ApiError);
    expect((fallo as ApiError).status).toBe(502);
    expect((fallo as ApiError).error).toBeUndefined();
  });
});
