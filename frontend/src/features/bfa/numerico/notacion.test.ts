import { describe, expect, it } from 'vitest';
import { lecturaAccesible, operacionSimple, tokenizar } from './notacion';

describe('notación numérica', () => {
  it('clasifica enteros, decimales, fracciones, operadores y el término faltante', () => {
    expect(tokenizar('45 − ___ = 18').map((t) => t.tipo)).toEqual([
      'numero',
      'operador',
      'casilla',
      'operador',
      'numero',
    ]);
    expect(tokenizar('2.5 + □ = 4,75').map((t) => t.tipo)).toEqual([
      'numero',
      'operador',
      'casilla',
      'operador',
      'numero',
    ]);
    expect(tokenizar('( 8 × ? ) ÷ 2 = 1/2').map((t) => t.tipo)).toEqual([
      'operador',
      'numero',
      'operador',
      'casilla',
      'operador',
      'operador',
      'numero',
      'operador',
      'fraccion',
    ]);
  });

  it('conserva las cifras exactamente como llegan (sin redondear ni cambiar separadores)', () => {
    const textos = tokenizar('0.50 + 2,5 = 3,00').map((t) => t.texto);
    expect(textos).toEqual(['0.50', '+', '2,5', '=', '3,00']);
  });

  it('lee fracciones y negativos sin cambiar su valor', () => {
    const [fraccion] = tokenizar('−3/4');
    expect(fraccion).toEqual({
      tipo: 'fraccion',
      signo: '−',
      numerador: 3,
      denominador: 4,
      texto: '−3/4',
    });
    expect(lecturaAccesible(tokenizar('−3 + ___ = 1/2'))).toBe(
      'menos 3 más término faltante igual a 1 sobre 2',
    );
  });

  it('reconoce operaciones simples para la disposición vertical', () => {
    expect(operacionSimple(tokenizar('2408 + 395 = 2813'))?.operador).toBe('+');
    expect(operacionSimple(tokenizar('47 × 6 = 282'))?.operador).toBe('×');
    expect(operacionSimple(tokenizar('45 − ___ = 18'))).toBeNull();
  });
});
