import { afterEach, describe, expect, it, vi } from 'vitest';
import { servicioEvaluacion } from './evaluacion';

const responder = (status: number, cuerpo: unknown) => {
  const simulado = vi.fn().mockResolvedValue(new Response(JSON.stringify(cuerpo), { status }));
  vi.stubGlobal('fetch', simulado);
  return simulado;
};

afterEach(() => vi.unstubAllGlobals());

describe('servicioEvaluacion (llamadas reales)', () => {
  it('E3 hace POST /api/v1/evaluaciones con el cuerpo JSON', async () => {
    const fetchSimulado = responder(201, { id: 15 });
    const solicitud = { versionFormularioId: 10, sujetoId: 25, periodoId: 3 };
    await expect(servicioEvaluacion.iniciarEvaluacion(solicitud)).resolves.toEqual({ id: 15 });
    expect(fetchSimulado).toHaveBeenCalledWith(
      '/api/v1/evaluaciones',
      expect.objectContaining({ method: 'POST', body: JSON.stringify(solicitud) }),
    );
  });

  it.each([
    ['E4', () => servicioEvaluacion.obtenerEvaluacion(15), '/api/v1/evaluaciones/15'],
    ['E2', () => servicioEvaluacion.obtenerTest(1), '/api/v1/tests/1'],
    ['E19', () => servicioEvaluacion.obtenerSujeto(25), '/api/v1/sujetos/25'],
    [
      'E21',
      () => servicioEvaluacion.listarPeriodosDisponibles(),
      '/api/v1/periodos?disponible=true',
    ],
  ])('%s llama a la ruta de DOC-05', async (_nombre, llamar, ruta) => {
    const fetchSimulado = responder(200, {});
    await llamar();
    expect(fetchSimulado).toHaveBeenCalledWith(ruta, expect.anything());
  });

  it('propaga el error del backend con su código', async () => {
    responder(404, {
      timestamp: 't',
      status: 404,
      codigo: 'EVALUACION_NO_ENCONTRADA',
      mensaje: 'No existe.',
      ruta: '/api/v1/evaluaciones/9',
    });
    await expect(servicioEvaluacion.obtenerEvaluacion(9)).rejects.toMatchObject({
      status: 404,
      error: { codigo: 'EVALUACION_NO_ENCONTRADA' },
    });
  });
});
