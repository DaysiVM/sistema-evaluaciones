import type { EstadoCalculo, EstadoNormativo, EstadoResultado, ResultadoFactor } from './tipos';

/**
 * Traducción de los estados de E9 a textos de la interfaz. Solo describe lo recibido:
 * no calcula, redondea ni completa valores. No contiene reglas de un test concreto.
 * Si el backend envía un estado desconocido, se muestra su código tal cual.
 */

export type Tono = 'exito' | 'aviso' | 'info' | 'neutro';

interface Descripcion {
  etiqueta: string;
  tono: Tono;
}

const ESTADOS_RESULTADO: Record<EstadoResultado, Descripcion & { detalle: string }> = {
  DISPONIBLE: {
    etiqueta: 'Resultado disponible',
    tono: 'exito',
    detalle: 'Se muestra la revisión vigente publicada.',
  },
  PENDIENTE_VALIDACION: {
    etiqueta: 'Pendiente de validación',
    tono: 'aviso',
    detalle:
      'Todavía no hay una revisión publicada. Los resultados se mostrarán cuando una revisión validada se publique.',
  },
  CALCULANDO: {
    etiqueta: 'En cálculo',
    tono: 'info',
    detalle: 'El servidor aún está calculando. Vuelva a consultar en unos minutos.',
  },
};

const ESTADOS_CALCULO: Record<EstadoCalculo, Descripcion> = {
  CALCULADO: { etiqueta: 'Calculado', tono: 'exito' },
  NO_CALCULABLE: { etiqueta: 'No calculable', tono: 'neutro' },
  PENDIENTE_VALIDACION: { etiqueta: 'Pendiente de validación', tono: 'aviso' },
};

const ESTADOS_NORMATIVOS: Record<EstadoNormativo, Descripcion> = {
  CONVERTIDO: { etiqueta: 'Convertido', tono: 'exito' },
  SIN_COINCIDENCIA: { etiqueta: 'Sin coincidencia en el baremo', tono: 'aviso' },
  SIN_BAREMO_VALIDADO: { etiqueta: 'Sin baremo validado', tono: 'aviso' },
  NO_APLICA: { etiqueta: 'No aplica', tono: 'neutro' },
};

const desconocido = (codigo: string): Descripcion => ({ etiqueta: codigo, tono: 'neutro' });

export function describirEstadoResultado(estado: string) {
  return (
    ESTADOS_RESULTADO[estado as EstadoResultado] ?? {
      ...desconocido(estado),
      detalle: 'Estado no reconocido por la interfaz.',
    }
  );
}

export const describirEstadoCalculo = (estado: string): Descripcion =>
  ESTADOS_CALCULO[estado as EstadoCalculo] ?? desconocido(estado);

export const describirEstadoNormativo = (estado: string): Descripcion =>
  ESTADOS_NORMATIVOS[estado as EstadoNormativo] ?? desconocido(estado);

/** Marca visible cuando el backend no envía un valor; un cero recibido se muestra como 0. */
export const SIN_VALOR = '—';

export const valorVisible = (valor: number | string | null | undefined): string =>
  valor === null || valor === undefined ? SIN_VALOR : String(valor);

/** Texto alternativo de la puntuación cuando no viene, según el estado de cálculo recibido. */
export function textoPuntuacion(factor: ResultadoFactor): string {
  if (factor.puntuacionDirecta !== null) return String(factor.puntuacionDirecta);
  return factor.estadoCalculo === 'CALCULADO'
    ? SIN_VALOR
    : describirEstadoCalculo(factor.estadoCalculo).etiqueta;
}

/** Hay revisión publicada solo si el backend la identifica; no se elige otra revisión. */
export const tieneRevisionPublicada = (r: {
  calificacionId: number | null;
  revision: number | null;
}) => r.calificacionId !== null && r.revision !== null;
