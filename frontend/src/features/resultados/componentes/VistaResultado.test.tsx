import { renderToStaticMarkup } from 'react-dom/server';
import { describe, expect, it } from 'vitest';
import { crearClienteResultadosMock } from '../../../services/resultados.mock';
import type { ResultadoResponse } from '../tipos';
import { VistaResultado } from './VistaResultado';

const cliente = crearClienteResultadosMock({ retardoMs: 0 });
const html = (resultado: ResultadoResponse, extra = {}) =>
  renderToStaticMarkup(<VistaResultado resultado={resultado} {...extra} />);

/** Celdas numéricas (puntuación y percentil) en el HTML renderizado. */
const numeros = (marcado: string) =>
  [...marcado.matchAll(/class="res-numero">([^<]*)</g)].map((m) => m[1]);

describe('VistaResultado', () => {
  it('presenta exactamente los valores recibidos, sin calcular ni completar', async () => {
    const r = await cliente.obtenerResultado(16);
    const esperados = r.factores.flatMap((f) => [f.puntuacionDirecta, f.percentil]);
    const mostrados = numeros(html(r));
    expect(mostrados).toHaveLength(esperados.length);
    mostrados.forEach((texto, i) => {
      const valor = esperados[i];
      if (valor === null) expect(texto).not.toMatch(/\d/);
      else expect(texto).toBe(String(valor));
    });
  });

  it('distingue no calculable y sin coincidencia, con su motivo', async () => {
    const marcado = html(await cliente.obtenerResultado(16));
    expect(marcado).toContain('No calculable');
    expect(marcado).toContain('Sin coincidencia en el baremo');
    expect(marcado).toContain('Falta un componente: FV.');
  });

  it.each([17, 18])('%i: sin revisión publicada no hay tabla ni puntuaciones', async (id) => {
    const marcado = html(await cliente.obtenerResultado(id));
    expect(marcado).not.toContain('res-tabla');
    expect(marcado).toContain('No hay una revisión publicada');
  });

  it('un estado pendiente no muestra factores aunque el backend los envíe', async () => {
    const r = await cliente.obtenerResultado(16);
    const marcado = html({ ...r, estadoResultado: 'PENDIENTE_VALIDACION' });
    expect(marcado).not.toContain('res-tabla');
    expect(marcado).toContain('Pendiente de validación');
  });

  it('usa el código del backend por defecto y acepta nombres inyectados por un módulo de test', async () => {
    const r = await cliente.obtenerResultado(16);
    expect(html(r)).toContain('>VOC1<');
    expect(html(r, { etiquetaFactor: (c: string) => `Nombre ${c}` })).toContain('Nombre VOC1');
  });

  it('avisa cuando los datos son simulados', async () => {
    expect(html(await cliente.obtenerResultado(16), { datosSimulados: true })).toContain(
      'Datos simulados',
    );
  });
});
