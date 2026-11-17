import { describe, expect, it } from 'vitest';
import { ApiError } from '../../services/http';
import { aErrorResultados, esNoFinalizada } from './errores';

const apiError = (status: number, codigo: string) =>
  new ApiError(status, { timestamp: '', status, codigo, mensaje: 'backend', ruta: '/x' });

describe('errores de E9', () => {
  it.each([
    [403, 'SIN_PERMISO', 'acceso'],
    [404, 'EVALUACION_NO_ENCONTRADA', 'no_encontrado'],
    [409, 'EVALUACION_NO_FINALIZADA', 'conflicto'],
    [500, 'ERROR_INTERNO', 'servidor'],
  ])('%i %s → %s', (status, codigo, tipo) => {
    const error = aErrorResultados(apiError(status, codigo));
    expect(error.tipo).toBe(tipo);
    expect(error.codigo).toBe(codigo);
  });

  it('usa mensajes propios de resultados', () => {
    expect(aErrorResultados(apiError(403, 'SIN_PERMISO')).mensaje).toMatch(/resultados/);
    expect(esNoFinalizada(aErrorResultados(apiError(409, 'EVALUACION_NO_FINALIZADA')))).toBe(true);
  });

  it('clasifica la falta de red como conexión', () => {
    expect(aErrorResultados(new TypeError('Failed to fetch')).tipo).toBe('conexion');
  });
});
