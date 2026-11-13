import { afterEach, describe, expect, it, vi } from 'vitest';
import { ApiError } from './http';
import { servicioResultados } from './resultados';

afterEach(() => vi.unstubAllGlobals());

describe('servicioResultados (llamada real)', () => {
  it('E9 hace GET /api/v1/evaluaciones/{id}/resultados y devuelve la respuesta sin cambios', async () => {
    const cuerpo = { evaluacionId: 16, estadoResultado: 'DISPONIBLE', factores: [] };
    const fetchSimulado = vi
      .fn()
      .mockResolvedValue(new Response(JSON.stringify(cuerpo), { status: 200 }));
    vi.stubGlobal('fetch', fetchSimulado);
    await expect(servicioResultados.obtenerResultado(16)).resolves.toEqual(cuerpo);
    expect(fetchSimulado).toHaveBeenCalledWith(
      '/api/v1/evaluaciones/16/resultados',
      expect.anything(),
    );
  });

  it('propaga el error del backend como ApiError', async () => {
    const cuerpo = { status: 403, codigo: 'SIN_PERMISO', mensaje: 'x', ruta: '', timestamp: '' };
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(new Response(JSON.stringify(cuerpo), { status: 403 })),
    );
    await expect(servicioResultados.obtenerResultado(16)).rejects.toBeInstanceOf(ApiError);
  });
});
