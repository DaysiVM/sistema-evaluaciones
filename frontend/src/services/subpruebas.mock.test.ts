import { describe, expect, it } from 'vitest';
import { ApiError } from './http';
import { crearClienteSubpruebasMock } from './subpruebas.mock';

function reloj(inicio = 1_000_000) {
  let t = inicio;
  return { ahora: () => t, avanzar: (s: number) => (t += s * 1000) };
}

async function codigoDeError(promesa: Promise<unknown>) {
  try {
    await promesa;
  } catch (error) {
    return error instanceof ApiError ? error.error?.codigo : 'OTRO';
  }
  return 'SIN_ERROR';
}

describe('mock de E5/E6/E7 de la familia Numérico', () => {
  it('usa los tiempos oficiales: 240 s en N1 y 360 s en N2', async () => {
    const r = reloj();
    const cliente = crearClienteSubpruebasMock({
      retardoMs: 0,
      ahora: r.ahora,
      validarOrden: false,
    });
    expect((await cliente.iniciar(1, 'N1-A')).segundosRestantes).toBe(240);
    expect((await cliente.iniciar(2, 'N1-B')).segundosRestantes).toBe(240);
    expect((await cliente.iniciar(3, 'N2-A')).segundosRestantes).toBe(360);
    expect((await cliente.iniciar(4, 'N2-B')).segundosRestantes).toBe(360);
  });

  it('respeta el orden y no permite dos subpruebas activas', async () => {
    const r = reloj();
    const cliente = crearClienteSubpruebasMock({ retardoMs: 0, ahora: r.ahora });
    expect(await codigoDeError(cliente.iniciar(1, 'N1-B'))).toBe('SUBPRUEBA_FUERA_DE_ORDEN');
    await cliente.iniciar(1, 'N1-A');
    expect(await codigoDeError(cliente.iniciar(1, 'N1-B'))).toBe('HAY_SUBPRUEBA_ACTIVA');
  });

  it('E6 no entrega preguntas antes de E5 ni incluye claves', async () => {
    const r = reloj();
    const cliente = crearClienteSubpruebasMock({ retardoMs: 0, ahora: r.ahora });
    expect(await codigoDeError(cliente.obtenerPreguntas(1, 'N1-A'))).toBe('SUBPRUEBA_NO_ACTIVA');
    await cliente.iniciar(1, 'N1-A');
    const { preguntas } = await cliente.obtenerPreguntas(1, 'N1-A');
    expect(preguntas.length).toBeGreaterThan(0);
    const json = JSON.stringify(preguntas);
    expect(json).not.toMatch(/correct|clave|acierto|puntuaci/i);
  });

  it('E7 guarda la respuesta, la devuelve en E6 y valida la opción', async () => {
    const r = reloj();
    const cliente = crearClienteSubpruebasMock({ retardoMs: 0, ahora: r.ahora });
    await cliente.iniciar(1, 'N1-A');
    const [p] = (await cliente.obtenerPreguntas(1, 'N1-A')).preguntas;
    r.avanzar(10);
    const avance = await cliente.registrarRespuestas(1, 'N1-A', [
      { preguntaId: p.id, opcion: 'B' },
    ]);
    expect(avance).toEqual({
      codigo: 'N1-A',
      registradas: 1,
      totalRespondidas: 1,
      segundosRestantes: 230,
    });
    expect((await cliente.obtenerPreguntas(1, 'N1-A')).preguntas[0].respuestaActual).toBe('B');
    expect(
      await codigoDeError(
        cliente.registrarRespuestas(1, 'N1-A', [{ preguntaId: p.id, opcion: 'C' }]),
      ),
    ).toBe('RESPUESTA_INVALIDA');
  });

  it('al vencer el tiempo rechaza respuestas y no permite volver a iniciar', async () => {
    const r = reloj();
    const cliente = crearClienteSubpruebasMock({ retardoMs: 0, ahora: r.ahora });
    await cliente.iniciar(1, 'N1-A');
    const [p] = (await cliente.obtenerPreguntas(1, 'N1-A')).preguntas;
    r.avanzar(240);
    expect(
      await codigoDeError(
        cliente.registrarRespuestas(1, 'N1-A', [{ preguntaId: p.id, opcion: 'A' }]),
      ),
    ).toBe('TIEMPO_AGOTADO');
    expect(await codigoDeError(cliente.obtenerPreguntas(1, 'N1-A'))).toBe('SUBPRUEBA_NO_ACTIVA');
    expect(await codigoDeError(cliente.iniciar(1, 'N1-A'))).toBe('SUBPRUEBA_CERRADA');
    // Cerrada N1-A, sigue N1-B en el orden oficial.
    expect((await cliente.iniciar(1, 'N1-B')).estado).toBe('ACTIVA');
  });

  it('repetir E5 sobre la subprueba activa no reinicia el tiempo', async () => {
    const r = reloj();
    const cliente = crearClienteSubpruebasMock({ retardoMs: 0, ahora: r.ahora });
    const primero = await cliente.iniciar(1, 'N1-A');
    r.avanzar(30);
    const segundo = await cliente.iniciar(1, 'N1-A');
    expect(segundo.inicio).toBe(primero.inicio);
    expect(segundo.segundosRestantes).toBe(210);
  });
});
