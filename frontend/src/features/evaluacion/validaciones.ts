import type { IniciarEvaluacionRequest, TestDetalleResponse } from './tipos';

/** Valores tal como los escribe el usuario en el formulario de inicio. */
export interface ValoresInicio {
  sujetoId: string;
  periodoId: string;
  aplicacion: 'completa' | 'parcial';
  subpruebas: string[];
  letraInicial: string;
}

export type CampoInicio = keyof ValoresInicio;

export type ErroresInicio = Partial<Record<CampoInicio, string>>;

export const VALORES_INICIO_VACIOS: ValoresInicio = {
  sujetoId: '',
  periodoId: '',
  aplicacion: 'completa',
  subpruebas: [],
  letraInicial: '',
};

export type ResultadoValidacion =
  { valido: true; solicitud: IniciarEvaluacionRequest } | { valido: false; errores: ErroresInicio };

const ENTERO_POSITIVO = /^[1-9]\d*$/;
const UNA_LETRA = /^[A-Za-zÑñ]$/;

/** Mensaje de error para un ID escrito a mano, o null si es válido. */
export function validarId(texto: string, nombre: string): string | null {
  const limpio = texto.trim();
  if (limpio === '') return `Ingrese el ID ${nombre}.`;
  if (!ENTERO_POSITIVO.test(limpio)) return `El ID ${nombre} debe ser un número entero positivo.`;
  return null;
}

/** FV entra si la aplicación es completa o si se marcó explícitamente. */
export function incluyeFluidez(valores: ValoresInicio, test: TestDetalleResponse): boolean {
  const tieneFv = test.subpruebas.some((s) => s.codigo === 'FV');
  if (!tieneFv) return false;
  return valores.aplicacion === 'completa' || valores.subpruebas.includes('FV');
}

/**
 * Valida el formulario y, si todo está bien, arma la solicitud de E3.
 * Esta validación es de forma; las reglas de negocio las confirma el servidor.
 */
export function validarInicio(
  valores: ValoresInicio,
  test: TestDetalleResponse,
  sujetoConfirmadoId: number | null,
): ResultadoValidacion {
  const errores: ErroresInicio = {};

  const errorSujeto = validarId(valores.sujetoId, 'del evaluado');
  if (errorSujeto) errores.sujetoId = errorSujeto;
  else if (sujetoConfirmadoId !== Number(valores.sujetoId.trim()))
    errores.sujetoId = 'Busque y confirme al evaluado antes de iniciar.';

  if (valores.periodoId === '') errores.periodoId = 'Seleccione un período.';

  if (valores.aplicacion === 'parcial' && valores.subpruebas.length === 0)
    errores.subpruebas = 'Seleccione al menos una subprueba.';

  const conFv = incluyeFluidez(valores, test);
  const letra = valores.letraInicial.trim();
  if (conFv && !UNA_LETRA.test(letra))
    errores.letraInicial = 'Fluidez Verbal requiere una sola letra inicial.';

  if (Object.keys(errores).length > 0) return { valido: false, errores };

  // Orden oficial del cuadernillo, sin duplicados.
  const subpruebas =
    valores.aplicacion === 'parcial'
      ? test.subpruebas.filter((s) => valores.subpruebas.includes(s.codigo)).map((s) => s.codigo)
      : undefined;

  return {
    valido: true,
    solicitud: {
      testId: test.id,
      versionFormularioId: test.versionFormularioId,
      sujetoId: Number(valores.sujetoId.trim()),
      periodoId: Number(valores.periodoId),
      ...(subpruebas ? { subpruebas } : {}),
      ...(conFv ? { fluidez: { letraInicial: letra.toUpperCase() } } : {}),
    },
  };
}
