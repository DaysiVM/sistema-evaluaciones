import { describe, expect, it } from 'vitest';
import type { http } from './http';
import { crearClienteSubpruebas } from './subpruebas';

type Llamada = { ruta: string; opciones?: RequestInit };

function clienteFalso() {
  const llamadas: Llamada[] = [];
  const falso = (async (ruta: string, opciones?: RequestInit) => {
    llamadas.push({ ruta, opciones });
    return {};
  }) as typeof http;
  return { llamadas, cliente: crearClienteSubpruebas(falso) };
}

describe('servicio de subpruebas (E5, E6, E7)', () => {
  it('E5 inicia con POST y sin cuerpo', async () => {
    const { llamadas, cliente } = clienteFalso();
    await cliente.iniciar(15, 'N1-A');
    expect(llamadas[0].ruta).toBe('/evaluaciones/15/subpruebas/N1-A/iniciar');
    expect(llamadas[0].opciones?.method).toBe('POST');
    expect(llamadas[0].opciones?.body).toBeUndefined();
  });

  it('E6 consulta las preguntas con GET', async () => {
    const { llamadas, cliente } = clienteFalso();
    await cliente.obtenerPreguntas(15, 'N2-A');
    expect(llamadas[0].ruta).toBe('/evaluaciones/15/subpruebas/N2-A/preguntas');
    expect(llamadas[0].opciones?.method).toBeUndefined();
  });

  it('E7 registra respuestas con PUT y el cuerpo de DOC-05', async () => {
    const { llamadas, cliente } = clienteFalso();
    await cliente.registrarRespuestas(15, 'N2-B', [{ preguntaId: 120, opcion: 'C' }]);
    expect(llamadas[0].ruta).toBe('/evaluaciones/15/subpruebas/N2-B/respuestas');
    expect(llamadas[0].opciones?.method).toBe('PUT');
    expect(JSON.parse(String(llamadas[0].opciones?.body))).toEqual({
      respuestas: [{ preguntaId: 120, opcion: 'C' }],
    });
  });
});
