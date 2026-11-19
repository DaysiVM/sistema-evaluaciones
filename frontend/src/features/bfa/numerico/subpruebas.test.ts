import { describe, expect, it } from 'vitest';
import { formatearTiempo, letraDeTecla, siguienteSubprueba, CONFIG_NUMERICO } from './subpruebas';

describe('configuración de la familia Numérico', () => {
  it('sigue el orden oficial y termina después de N2-B', () => {
    expect(siguienteSubprueba('N1-A')).toBe('N1-B');
    expect(siguienteSubprueba('N1-B')).toBe('N2-A');
    expect(siguienteSubprueba('N2-A')).toBe('N2-B');
    expect(siguienteSubprueba('N2-B')).toBeNull();
  });

  it('traduce las teclas a la letra de la respuesta', () => {
    expect(letraDeTecla(CONFIG_NUMERICO['N1-A'], 'v')).toBe('A');
    expect(letraDeTecla(CONFIG_NUMERICO['N1-A'], 'f')).toBe('B');
    expect(letraDeTecla(CONFIG_NUMERICO['N1-A'], 'c')).toBeNull();
    expect(letraDeTecla(CONFIG_NUMERICO['N2-A'], 'd')).toBe('D');
    expect(letraDeTecla(CONFIG_NUMERICO['N2-A'], 'e')).toBeNull();
  });

  it('formatea el tiempo restante', () => {
    expect(formatearTiempo(240)).toBe('04:00');
    expect(formatearTiempo(5)).toBe('00:05');
    expect(formatearTiempo(-3)).toBe('00:00');
    expect(formatearTiempo(null)).toBe('—');
  });
});
