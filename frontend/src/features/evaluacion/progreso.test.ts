import { describe, expect, it } from 'vitest';
import { calcularProgreso, formatearFechaHora, formatearSegundos } from './progreso';
import type { EvaluacionResponse } from './tipos';

const evaluacion = (subpruebas: EvaluacionResponse['subpruebas']): EvaluacionResponse => ({
  id: 15,
  testId: 1,
  sujetoId: 25,
  periodoId: 3,
  estado: 'EN_CURSO',
  fechaInicio: '2026-10-30T15:30:00Z',
  fechaFin: null,
  subpruebaActiva: null,
  subpruebas,
});

describe('calcularProgreso', () => {
  it('cuenta las subpruebas cerradas sobre las incluidas', () => {
    const e = evaluacion([
      { codigo: 'VOC1', estado: 'CERRADA' },
      { codigo: 'VOC2', estado: 'ACTIVA' },
      { codigo: 'CV', estado: 'PENDIENTE' },
    ]);
    expect(calcularProgreso(e)).toEqual({ cerradas: 1, total: 3, porcentaje: 33 });
  });

  it('sin subpruebas el progreso es 0', () => {
    expect(calcularProgreso(evaluacion([]))).toEqual({ cerradas: 0, total: 0, porcentaje: 0 });
  });
});

describe('formatearSegundos', () => {
  it.each([
    [214, '3:34'],
    [5070, '84:30'],
    [0, '0:00'],
    [-5, '0:00'],
  ])('%i → %s', (segundos, texto) => {
    expect(formatearSegundos(segundos)).toBe(texto);
  });
});

describe('formatearFechaHora', () => {
  it('devuelve el texto original si la fecha no es válida', () => {
    expect(formatearFechaHora('no-es-fecha')).toBe('no-es-fecha');
  });
});
