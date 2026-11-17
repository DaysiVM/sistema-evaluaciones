import { SIN_VALOR, describirEstadoCalculo } from '../../resultados/presentacion';
import type { ResultadoFactor } from '../../resultados/tipos';
import { CODIGOS_PERFIL } from './catalogo';

/**
 * Reglas de presentación del perfil BFA (UI-05). Solo deciden QUÉ se muestra de lo que
 * envía E9; nunca calculan, redondean, interpolan ni completan valores.
 */

/**
 * La puntuación directa solo se muestra si el factor está CALCULADO.
 * Un factor pendiente no se presenta como definitivo (Documento BFA, FV pendiente),
 * aunque el backend enviara un número. Un 0 recibido se muestra como 0.
 */
export function textoPuntuacionDirecta(f: ResultadoFactor): string {
  // Texto corto en la celda; la insignia de estado muestra la etiqueta completa.
  if (f.estadoCalculo === 'PENDIENTE_VALIDACION') return 'Pendiente';
  if (f.estadoCalculo !== 'CALCULADO') return describirEstadoCalculo(f.estadoCalculo).etiqueta;
  return f.puntuacionDirecta === null ? SIN_VALOR : String(f.puntuacionDirecta);
}

/** El percentil solo existe con CALCULADO + CONVERTIDO y un valor recibido (E9, ENT-BFA-10). */
export const percentilVisible = (f: ResultadoFactor): number | null =>
  f.estadoCalculo === 'CALCULADO' && f.estadoNormativo === 'CONVERTIDO' ? f.percentil : null;

/**
 * Percentil con la marca «Pc», nunca con «%»: el percentil no equivale al porcentaje
 * de aciertos (ENT-BFA-10).
 */
export function textoPercentil(f: ResultadoFactor): string {
  const p = percentilVisible(f);
  return p === null ? SIN_VALOR : `Pc ${p}`;
}

export interface ResumenPerfil {
  total: number;
  calculados: number;
  conPercentil: number;
  parcial: boolean;
}

/**
 * Conteo de estados recibidos (no es un cálculo psicométrico). El perfil es «parcial»
 * cuando no todos los factores del catálogo llegan CALCULADOS (p. ej. aplicación parcial).
 */
export function resumirPerfil(factores: ResultadoFactor[]): ResumenPerfil {
  const porCodigo = new Map(factores.map((f) => [f.codigo, f]));
  const delCatalogo = CODIGOS_PERFIL.map((c) => porCodigo.get(c));
  const calculados = delCatalogo.filter((f) => f?.estadoCalculo === 'CALCULADO').length;
  const conPercentil = delCatalogo.filter((f) => f && percentilVisible(f) !== null).length;
  return {
    total: CODIGOS_PERFIL.length,
    calculados,
    conPercentil,
    parcial: calculados < CODIGOS_PERFIL.length,
  };
}
