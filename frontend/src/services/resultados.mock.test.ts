import { describe, expect, it } from 'vitest';
import { ApiError } from './http';
import { crearClienteResultadosMock } from './resultados.mock';

const cliente = crearClienteResultadosMock({ retardoMs: 0 });

const codigoDe = async (id: number) => {
  try {
    await cliente.obtenerResultado(id);
    return 'OK';
  } catch (e) {
    return e instanceof ApiError ? e.error?.codigo : (e as Error).name;
  }
};

describe('mock de E9', () => {
  it('16: disponible con revisión y factores de todos los estados', async () => {
    const r = await cliente.obtenerResultado(16);
    expect(r.estadoResultado).toBe('DISPONIBLE');
    expect(r.revision).not.toBeNull();
    const estados = new Set(r.factores.map((f) => f.estadoCalculo));
    expect(estados).toEqual(new Set(['CALCULADO', 'NO_CALCULABLE']));
    // Reglas de E9: no calculable ⇒ puntuación nula; percentil solo si CONVERTIDO.
    for (const f of r.factores) {
      if (f.estadoCalculo === 'NO_CALCULABLE') expect(f.puntuacionDirecta).toBeNull();
      if (f.estadoNormativo !== 'CONVERTIDO') expect(f.percentil).toBeNull();
      if (f.percentil === null) expect(f.motivo).toBeTruthy();
    }
  });

  it.each([17, 18])('%i: sin revisión publicada, factores vacío', async (id) => {
    const r = await cliente.obtenerResultado(id);
    expect(r.calificacionId).toBeNull();
    expect(r.revision).toBeNull();
    expect(r.factores).toEqual([]);
  });

  it.each([
    [15, 'EVALUACION_NO_FINALIZADA'],
    [403, 'SIN_PERMISO'],
    [500, 'ERROR_INTERNO'],
    [999, 'EVALUACION_NO_ENCONTRADA'],
    [503, 'TypeError'],
  ])('%i → %s', async (id, codigo) => {
    expect(await codigoDe(id)).toBe(codigo);
  });

  it('devuelve copias: modificar la respuesta no altera el mock', async () => {
    const r = await cliente.obtenerResultado(16);
    r.factores[0].puntuacionDirecta = 999;
    expect((await cliente.obtenerResultado(16)).factores[0].puntuacionDirecta).not.toBe(999);
  });
});
