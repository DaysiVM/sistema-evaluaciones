import { renderToStaticMarkup } from 'react-dom/server';
import { describe, expect, it } from 'vitest';
import { ExpresionMatematica, TextoMatematico } from './Notacion';

const html = (nodo: React.ReactElement) => renderToStaticMarkup(nodo);

describe('ExpresionMatematica', () => {
  it('apila las fracciones y marca la casilla del término faltante', () => {
    const marcado = html(<ExpresionMatematica expresion="1/2 + ___ = 3/4" />);
    expect(marcado.match(/class="num-fraccion"/g)).toHaveLength(2);
    expect(marcado).toContain('término faltante');
  });

  it('conserva decimales y el signo menos tal como llegan', () => {
    const marcado = html(<ExpresionMatematica expresion="2.5 + −3 = −0.5" />);
    expect(marcado).toContain('2.5');
    expect(marcado).toContain('−3');
    expect(marcado).toContain('−0.5');
  });

  it('dibuja en columna una operación simple cuando el formato es vertical', () => {
    const enLinea = html(<ExpresionMatematica expresion="12 + 7 = 19" />);
    const enColumna = html(<ExpresionMatematica expresion="12 + 7 = 19" formato="vertical" />);
    expect(enColumna).not.toBe(enLinea);
    expect(enColumna).toContain('19');
  });
});

describe('TextoMatematico', () => {
  it('apila las fracciones dentro del texto y deja el resto literal', () => {
    const marcado = html(<TextoMatematico texto="Compró 3/4 de litro por C$ 12.50." />);
    expect(marcado).toContain('class="num-fraccion"');
    expect(marcado).toContain('3 sobre 4');
    expect(marcado).toContain('C$ 12.50.');
  });
});
