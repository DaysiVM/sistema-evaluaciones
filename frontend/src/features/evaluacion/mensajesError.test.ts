import { describe, expect, it } from 'vitest';
import { ApiError } from '../../services/http';
import { aErrorMostrable, etiquetaCampo } from './mensajesError';

const apiError = (status: number, codigo: string) =>
  new ApiError(status, { timestamp: 't', status, codigo, mensaje: 'del servidor', ruta: '/x' });

describe('aErrorMostrable', () => {
  it.each([
    [400, 'SOLICITUD_INVALIDA', 'validacion'],
    [422, 'SUBPRUEBA_NO_INCLUIDA', 'validacion'],
    [401, 'NO_AUTENTICADO', 'acceso'],
    [403, 'SIN_PERMISO', 'acceso'],
    [404, 'SUJETO_NO_ENCONTRADO', 'no_encontrado'],
    [409, 'EVALUACION_EN_CURSO', 'conflicto'],
    [500, 'ERROR_INTERNO', 'servidor'],
    [502, 'X', 'conexion'],
  ])('%i %s → %s', (status, codigo, tipo) => {
    expect(aErrorMostrable(apiError(status, codigo))).toMatchObject({ tipo, codigo });
  });

  it('usa el mensaje propio para códigos conocidos y el del servidor para los demás', () => {
    expect(aErrorMostrable(apiError(403, 'SIN_PERMISO')).mensaje).not.toBe('del servidor');
    expect(aErrorMostrable(apiError(422, 'OTRO_CODIGO')).mensaje).toBe('del servidor');
  });

  it('conserva los detalles de validación', () => {
    const error = new ApiError(400, {
      timestamp: 't',
      status: 400,
      codigo: 'SOLICITUD_INVALIDA',
      mensaje: 'm',
      ruta: '/x',
      detalles: [{ campo: 'sujetoId', mensaje: 'es obligatorio' }],
    });
    expect(aErrorMostrable(error).detalles).toEqual([
      { campo: 'sujetoId', mensaje: 'es obligatorio' },
    ]);
  });

  it('un TypeError de fetch es un error de conexión', () => {
    expect(aErrorMostrable(new TypeError('Failed to fetch'))).toMatchObject({
      tipo: 'conexion',
      codigo: 'SIN_CONEXION',
    });
  });

  it('cualquier otro fallo se muestra como error del servidor', () => {
    expect(aErrorMostrable('raro')).toMatchObject({ tipo: 'servidor' });
  });
});

describe('etiquetaCampo', () => {
  it('traduce campos conocidos y deja los demás igual', () => {
    expect(etiquetaCampo('periodoId')).toBe('Período');
    expect(etiquetaCampo('otro')).toBe('otro');
  });
});
