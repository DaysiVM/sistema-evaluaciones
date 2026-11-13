import type { EvaluacionResponse } from './tipos';

export interface ResumenProgreso {
  cerradas: number;
  total: number;
  porcentaje: number;
}

/**
 * Progreso derivado solo de lo que devolvió E4 (subpruebas CERRADAS / incluidas).
 * Es una lectura para mostrar; no cambia el estado oficial.
 */
export function calcularProgreso(evaluacion: EvaluacionResponse): ResumenProgreso {
  const total = evaluacion.subpruebas.length;
  const cerradas = evaluacion.subpruebas.filter((s) => s.estado === 'CERRADA').length;
  const porcentaje = total === 0 ? 0 : Math.round((cerradas / total) * 100);
  return { cerradas, total, porcentaje };
}

/** 214 → "3:34". */
export function formatearSegundos(segundos: number): string {
  const s = Math.max(0, Math.floor(segundos));
  return `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
}

/** ISO-8601 (UTC) → fecha y hora local legible. */
export function formatearFechaHora(iso: string): string {
  const fecha = new Date(iso);
  if (Number.isNaN(fecha.getTime())) return iso;
  return fecha.toLocaleString('es-NI', { dateStyle: 'medium', timeStyle: 'short' });
}
