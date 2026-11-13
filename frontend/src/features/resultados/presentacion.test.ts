import { describe, expect, it } from 'vitest';
import {
  describirEstadoCalculo,
  describirEstadoNormativo,
  describirEstadoResultado,
  SIN_VALOR,
  textoPuntuacion,
  tieneRevisionPublicada,
  valorVisible,
} from './presentacion';
import type { ResultadoFactor } from './tipos';

const base: ResultadoFactor = {
  codigo: 'X1',
  puntuacionDirecta: null,
  estadoCalculo: 'NO_CALCULABLE',
  percentil: null,
  idRango: null,
  estadoNormativo: 'NO_APLICA',
  motivo: 'Falta un componente.',
};

describe('presentación de E9', () => {
  it('muestra un cero recibido como 0 y un nulo como guion', () => {
    expect(valorVisible(0)).toBe('0');
    expect(valorVisible(null)).toBe(SIN_VALOR);
  });

  it('no inventa puntuación para un factor no calculable', () => {
    expect(textoPuntuacion(base)).toBe('No calculable');
    expect(textoPuntuacion({ ...base, estadoCalculo: 'PENDIENTE_VALIDACION' })).toBe(
      'Pendiente de validación',
    );
    expect(textoPuntuacion({ ...base, puntuacionDirecta: 0, estadoCalculo: 'CALCULADO' })).toBe(
      '0',
    );
  });

  it('describe los estados documentados y muestra tal cual uno desconocido', () => {
    expect(describirEstadoResultado('DISPONIBLE').tono).toBe('exito');
    expect(describirEstadoResultado('PENDIENTE_VALIDACION').tono).toBe('aviso');
    expect(describirEstadoCalculo('CALCULADO').etiqueta).toBe('Calculado');
    expect(describirEstadoNormativo('SIN_COINCIDENCIA').etiqueta).toMatch(/Sin coincidencia/);
    expect(describirEstadoNormativo('OTRO_ESTADO').etiqueta).toBe('OTRO_ESTADO');
  });

  it('solo reconoce revisión publicada si el backend la identifica', () => {
    expect(tieneRevisionPublicada({ calificacionId: 31, revision: 1 })).toBe(true);
    expect(tieneRevisionPublicada({ calificacionId: null, revision: null })).toBe(false);
  });
});
