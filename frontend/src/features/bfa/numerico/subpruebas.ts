/**
 * Configuración de presentación de la familia Numérico (Documento BFA §2.6 y §5.9 a §5.12).
 * Solo describe cómo se muestra cada subprueba. Los tiempos los controla el backend (E5/E6/E7);
 * aquí no hay claves, puntuaciones ni reglas de calificación.
 */

export const CODIGOS_NUMERICO = ['N1-A', 'N1-B', 'N2-A', 'N2-B'] as const;
export type CodigoNumerico = (typeof CODIGOS_NUMERICO)[number];

export interface ConfigNumerica {
  codigo: CodigoNumerico;
  nombre: string;
  /** `binaria`: A = Verdadero / B = Falso. `multiple`: cuatro alternativas A a D. */
  respuesta: 'binaria' | 'multiple';
  /** Cómo se dibuja el enunciado. */
  contenido: 'operacion' | 'ecuacion' | 'problema';
  /** Letras que la subprueba admite (RF-NUM-SUM/MUL/OPE/PRO-01). */
  letras: readonly string[];
  /**
   * Volver a preguntas anteriores mientras la subprueba está activa. Sumas y Multiplicaciones
   * avanzan solas al responder (UI-04); en N2 se permite según el flujo de §5.2 (consulta C-1 abierta).
   */
  volverAtras: boolean;
  /** Texto de la fase de instrucciones (redacción propia a partir de §5.9 a §5.12). */
  instrucciones: string;
  /** Tiempo oficial documentado, solo para el texto de instrucciones. El reloj viene del backend. */
  tiempoOficial: string;
  etiqueta: (numero: number, total: number) => string;
}

const BINARIA = ['A', 'B'] as const;
const CUATRO = ['A', 'B', 'C', 'D'] as const;

export const CONFIG_NUMERICO: Record<CodigoNumerico, ConfigNumerica> = {
  'N1-A': {
    codigo: 'N1-A',
    nombre: 'Numérico · Sumas',
    respuesta: 'binaria',
    contenido: 'operacion',
    letras: BINARIA,
    volverAtras: false,
    tiempoOficial: '4 minutos',
    instrucciones:
      'Cada suma trae un resultado propuesto. Marca A si es verdadero o B si es falso; al responder pasas a la siguiente. Puedes usar las teclas A o V y B o F.',
    etiqueta: (n, total) => `Operación ${n} de ${total}`,
  },
  'N1-B': {
    codigo: 'N1-B',
    nombre: 'Numérico · Multiplicaciones',
    respuesta: 'binaria',
    contenido: 'operacion',
    letras: BINARIA,
    volverAtras: false,
    tiempoOficial: '4 minutos',
    instrucciones:
      'Cada multiplicación trae un resultado propuesto. Marca A si es verdadero o B si es falso; al responder pasas a la siguiente. Puedes usar las teclas A o V y B o F.',
    etiqueta: (n, total) => `Operación ${n} de ${total}`,
  },
  'N2-A': {
    codigo: 'N2-A',
    nombre: 'Numérico · Operaciones',
    respuesta: 'multiple',
    contenido: 'ecuacion',
    letras: CUATRO,
    volverAtras: true,
    tiempoOficial: '6 minutos',
    instrucciones:
      'En cada operación falta un término. Elige entre A, B, C y D la alternativa que la completa.',
    etiqueta: (n, total) => `Pregunta ${n} de ${total}`,
  },
  'N2-B': {
    codigo: 'N2-B',
    nombre: 'Numérico · Problemas',
    respuesta: 'multiple',
    contenido: 'problema',
    letras: CUATRO,
    volverAtras: true,
    tiempoOficial: '6 minutos',
    instrucciones: 'Lee cada problema, resuélvelo y elige la respuesta correcta entre A, B, C y D.',
    etiqueta: (n, total) => `Problema ${n} de ${total}`,
  },
};

export function esCodigoNumerico(codigo: string): codigo is CodigoNumerico {
  return (CODIGOS_NUMERICO as readonly string[]).includes(codigo);
}

/** Letra elegida por teclado, o `null` si la tecla no corresponde a la subprueba. */
export function letraDeTecla(config: ConfigNumerica, tecla: string): string | null {
  const t = tecla.toUpperCase();
  if (config.respuesta === 'binaria') {
    if (t === 'A' || t === 'V') return 'A';
    if (t === 'B' || t === 'F') return 'B';
    return null;
  }
  return config.letras.includes(t) ? t : null;
}

/** Subprueba que sigue en el orden oficial de la familia, o `null` después de N2-B. */
export function siguienteSubprueba(codigo: CodigoNumerico): CodigoNumerico | null {
  const posicion = CODIGOS_NUMERICO.indexOf(codigo);
  return CODIGOS_NUMERICO[posicion + 1] ?? null;
}

/** Tiempo en formato mm:ss, sin bajar de cero. */
export function formatearTiempo(segundos: number | null): string {
  if (segundos === null) return '—';
  const seguro = Math.max(0, segundos);
  return `${String(Math.floor(seguro / 60)).padStart(2, '0')}:${String(seguro % 60).padStart(2, '0')}`;
}
