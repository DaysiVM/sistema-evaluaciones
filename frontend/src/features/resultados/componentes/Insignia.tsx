import type { Tono } from '../presentacion';

/** Etiqueta de estado. El texto siempre acompaña al color (no depende solo del color). */
export function Insignia({ texto, tono }: { texto: string; tono: Tono }) {
  return <span className={`res-insignia res-insignia--${tono}`}>{texto}</span>;
}
