import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError, http } from './http';

const responder = (status: number, cuerpo?: unknown) =>
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response(cuerpo === undefined ? null : JSON.stringify(cuerpo), { status })));

afterEach(() => vi.unstubAllGlobals());

describe('http', () => {
  it('llama a /api/v1 y devuelve el JSON', async () => {
    responder(200, [{ id: 1 }]);
    await expect(http('/tests')).resolves.toEqual([{ id: 1 }]);
    expect(fetch).toHaveBeenCalledWith('/api/v1/tests', expect.anything());
  });

  it('convierte el error del backend en ApiError con su código', async () => {
    responder(409, { timestamp: 't', status: 409, codigo: 'EVALUACION_FINALIZADA', mensaje: 'Ya finalizó.', ruta: '/x' });
    await expect(http('/x')).rejects.toMatchObject({ status: 409, error: { codigo: 'EVALUACION_FINALIZADA' } });
  });

  it('sin cuerpo de error conserva el estado HTTP', async () => {
    responder(502);
    const fallo = await http('/x').catch((e: unknown) => e);
    expect(fallo).toBeInstanceOf(ApiError);
    expect((fallo as ApiError).status).toBe(502);
    expect((fallo as ApiError).error).toBeUndefined();
  });
});
