import { renderToStaticMarkup } from 'react-dom/server';
import { describe, expect, it } from 'vitest';
import { AvanceFamilia } from './AvanceFamilia';
import { FinFamilia } from './FinFamilia';

describe('AvanceFamilia', () => {
  it('muestra las cuatro subpruebas en orden con su estado, sin enlaces para saltar', () => {
    const marcado = renderToStaticMarkup(
      <AvanceFamilia
        estados={{
          'N1-A': 'cerrada',
          'N1-B': 'en-curso',
          'N2-A': 'pendiente',
          'N2-B': 'pendiente',
        }}
      />,
    );
    const codigos = [...marcado.matchAll(/num-avance-codigo">([^<]+)</g)].map((m) => m[1]);
    expect(codigos).toEqual(['N1-A', 'N1-B', 'N2-A', 'N2-B']);
    expect(marcado).toContain('aria-current="step"');
    expect(marcado).toContain('Cerrada');
    expect(marcado).not.toMatch(/<a |<button/);
  });
});

describe('FinFamilia', () => {
  it('confirma lo registrado sin mostrar resultados', () => {
    const marcado = renderToStaticMarkup(
      <FinFamilia
        resumen={{
          'N1-A': { respondidas: 38, total: 40 },
          'N1-B': { respondidas: 30, total: 30 },
          'N2-A': { respondidas: 6, total: 6 },
        }}
      />,
    );
    expect(marcado).toContain('Sección numérica terminada');
    expect(marcado).toContain('38 de 40');
    expect(marcado).toContain('>—<');
    expect(marcado).toContain('Los resultados no se muestran');
    expect(marcado).not.toMatch(/acierto|puntuaci|percentil|correct/i);
  });
});
