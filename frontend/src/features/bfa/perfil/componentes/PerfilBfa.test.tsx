import { renderToStaticMarkup } from 'react-dom/server';
import { describe, expect, it } from 'vitest';
import { ESCENARIOS_UI05 } from '../../../../dev/ui05-escenarios';
import type { ResultadoResponse } from '../../../resultados/tipos';
import { PerfilBfa } from './PerfilBfa';

const escenario = (id: string) => {
  const e = ESCENARIOS_UI05.find((x) => x.id === id);
  if (!e) throw new Error(id);
  return e.resultado;
};
const html = (r: ResultadoResponse) =>
  renderToStaticMarkup(<PerfilBfa resultado={r} datosSimulados />);
const celdas = (marcado: string, clase: string) =>
  [...marcado.matchAll(new RegExp(`class="[^"]*${clase}[^"]*">([^<]*)<`, 'g'))].map((m) => m[1]);
const barras = (marcado: string) => (marcado.match(/class="perfil-barra-relleno"/g) ?? []).length;

describe('PerfilBfa', () => {
  it('muestra las 18 PD exactamente como llegan en el escenario completo', () => {
    const r = escenario('completo');
    const pd = celdas(html(r), 'perfil-pd');
    expect(pd).toHaveLength(18);
    const esperadas = new Set(r.factores.map((f) => String(f.puntuacionDirecta)));
    pd.forEach((texto) => expect(esperadas).toContain(texto));
  });

  it('dibuja una marca de escala solo por cada percentil convertido', () => {
    const r = escenario('completo');
    const convertidos = r.factores.filter((f) => f.estadoNormativo === 'CONVERTIDO').length;
    expect(barras(html(r))).toBe(convertidos);
    expect(barras(html(escenario('sin-baremo')))).toBe(0);
  });

  it('nunca usa «%» para el percentil', () => {
    for (const e of ESCENARIOS_UI05)
      expect(celdas(html(e.resultado), 'perfil-pc').join('')).not.toContain('%');
  });

  it('marca el perfil parcial con el conteo de calculados', () => {
    const marcado = html(escenario('parcial'));
    expect(marcado).toContain('Perfil parcial');
    expect(marcado).toContain('10 de 18 calculados');
    expect(marcado).toContain('Subprueba no aplicada');
    expect(html(escenario('completo'))).toContain('Perfil completo');
  });

  it('no presenta la PD provisional de FV pendiente', () => {
    const marcado = html(escenario('fv-pendiente'));
    expect(marcado).toContain('Pendiente de validación');
    expect(celdas(marcado, 'perfil-pd')).not.toContain('28');
  });

  it('muestra un 0 real como 0', () => {
    expect(celdas(html(escenario('sin-baremo')), 'perfil-pd')).toContain('0');
  });

  it.each(['pendiente-validacion', 'calculando'])('%s: sin revisión no hay valores', (id) => {
    const marcado = html(escenario(id));
    expect(marcado).not.toContain('perfil-tabla');
    expect(marcado).not.toContain('perfil-compuesto');
    expect(marcado).toContain('No hay una revisión publicada');
  });

  it('no oculta un factor del catálogo que no llegó ni un código desconocido', () => {
    const r = escenario('completo');
    const marcado = html({
      ...r,
      factores: [
        ...r.factores.filter((f) => f.codigo !== 'R2'),
        { ...r.factores[0], codigo: 'X9' },
      ],
    });
    expect(marcado).toContain('No recibido');
    expect(marcado).toContain('Otros códigos recibidos');
    expect(marcado).toContain('X9');
  });

  it('no contiene etiquetas de interpretación', () => {
    const marcado = html(escenario('completo')).toLowerCase();
    for (const palabra of ['promedio', 'alto', 'bajo', 'superior', 'deficiente']) {
      expect(marcado).not.toMatch(new RegExp(`\\b${palabra}\\b`));
    }
  });
});
