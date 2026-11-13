import { describe, expect, it } from 'vitest';
import type { ConfiguracionSubprueba, TestDetalleResponse } from './tipos';
import {
  incluyeFluidez,
  validarId,
  validarInicio,
  VALORES_INICIO_VACIOS,
  type ValoresInicio,
} from './validaciones';

const sub = (orden: number, codigo: string): ConfiguracionSubprueba => ({
  codigo,
  nombre: codigo,
  orden,
  factor: codigo,
  tiempoLimiteSegundos: 300,
  tipoRespuesta: codigo === 'FV' ? 'TEXTO_LIBRE' : 'SELECCION',
  opciones: null,
  numeroPreguntas: 0,
  maximoEntradas: null,
  tienePractica: true,
});

const test: TestDetalleResponse = {
  id: 1,
  codigo: 'BFA',
  nombre: 'BFA',
  versionFormularioId: 10,
  subpruebas: [sub(1, 'VOC1'), sub(8, 'CV'), sub(13, 'FV')],
};

const base: ValoresInicio = { ...VALORES_INICIO_VACIOS, sujetoId: '25', periodoId: '3' };

describe('validarId', () => {
  it.each(['', '  ', '0', '-3', '1.5', 'abc', '01'])('rechaza %j', (texto) => {
    expect(validarId(texto, 'del evaluado')).not.toBeNull();
  });

  it('acepta un entero positivo con espacios', () => {
    expect(validarId(' 25 ', 'del evaluado')).toBeNull();
  });
});

describe('validarInicio', () => {
  it('marca todos los campos obligatorios', () => {
    const r = validarInicio(VALORES_INICIO_VACIOS, test, null);
    expect(r.valido).toBe(false);
    if (!r.valido) {
      expect(Object.keys(r.errores).sort()).toEqual(['letraInicial', 'periodoId', 'sujetoId']);
    }
  });

  it('exige que el evaluado esté confirmado y sea el mismo ID', () => {
    const r = validarInicio({ ...base, letraInicial: 'm' }, test, 26);
    expect(r).toMatchObject({ valido: false, errores: { sujetoId: expect.any(String) } });
  });

  it('arma una solicitud completa con la letra en mayúscula', () => {
    expect(validarInicio({ ...base, letraInicial: ' m ' }, test, 25)).toEqual({
      valido: true,
      solicitud: {
        testId: 1,
        versionFormularioId: 10,
        sujetoId: 25,
        periodoId: 3,
        fluidez: { letraInicial: 'M' },
      },
    });
  });

  it('en aplicación parcial ordena las subpruebas y omite la letra si no hay FV', () => {
    const r = validarInicio(
      { ...base, aplicacion: 'parcial', subpruebas: ['CV', 'VOC1'] },
      test,
      25,
    );
    expect(r).toEqual({
      valido: true,
      solicitud: {
        testId: 1,
        versionFormularioId: 10,
        sujetoId: 25,
        periodoId: 3,
        subpruebas: ['VOC1', 'CV'],
      },
    });
  });

  it('una aplicación parcial necesita al menos una subprueba', () => {
    const r = validarInicio({ ...base, aplicacion: 'parcial' }, test, 25);
    expect(r).toMatchObject({ valido: false, errores: { subpruebas: expect.any(String) } });
  });

  it.each(['', 'ab', '1'])('rechaza la letra %j cuando se incluye FV', (letra) => {
    const valores: ValoresInicio = {
      ...base,
      aplicacion: 'parcial',
      subpruebas: ['FV'],
      letraInicial: letra,
    };
    expect(validarInicio(valores, test, 25)).toMatchObject({
      valido: false,
      errores: { letraInicial: expect.any(String) },
    });
  });
});

describe('incluyeFluidez', () => {
  it('es verdadero en la aplicación completa y falso si FV no se eligió', () => {
    expect(incluyeFluidez(base, test)).toBe(true);
    expect(incluyeFluidez({ ...base, aplicacion: 'parcial', subpruebas: ['CV'] }, test)).toBe(
      false,
    );
  });
});
