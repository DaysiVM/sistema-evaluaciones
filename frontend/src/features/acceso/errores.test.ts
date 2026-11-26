import { describe, expect, it } from 'vitest';
import { ApiError } from '../../services/http';
import { aErrorAcceso } from './errores';

const cuerpo = (status: number, codigo: string) => ({
  timestamp: '2099-01-01T00:00:00Z',
  status,
  codigo,
  mensaje: 'Mensaje ficticio',
  ruta: '/api/v1/auth/recuperacion',
});

describe('errores de acceso', () => {
  it('E16 503 CORREO_NO_DISPONIBLE no se muestra como falta de conexión', () => {
    const error = aErrorAcceso(new ApiError(503, cuerpo(503, 'CORREO_NO_DISPONIBLE')));
    expect(error.tipo).toBe('servidor');
    expect(error.codigo).toBe('CORREO_NO_DISPONIBLE');
    expect(error.mensaje).toMatch(/correo de recuperación/);
  });

  it('un 503 sin cuerpo del backend sigue siendo falta de conexión', () => {
    expect(aErrorAcceso(new ApiError(503)).tipo).toBe('conexion');
  });

  it('los demás errores usan la traducción común', () => {
    const error = aErrorAcceso(new ApiError(400, cuerpo(400, 'TOKEN_INVALIDO')));
    expect(error.tipo).toBe('validacion');
    expect(error.mensaje).toBe('Mensaje ficticio');
  });
});
