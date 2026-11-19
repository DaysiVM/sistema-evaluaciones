/**
 * Notación matemática de la familia Numérico (RNF-NUM-OPE-01: enteros, decimales y fracciones
 * sin pérdida de formato). Solo PRESENTA el texto que envía el backend: no calcula, no redondea
 * y no cambia separadores ni signos. Las cifras se muestran exactamente como llegan.
 *
 * Formato del texto (supuesto; E6 solo define `enunciado` como texto): términos separados por
 * espacios, fracción como `a/b`, término faltante como `___`, `□` o `?`.
 */

export type Token =
  | { tipo: 'numero'; texto: string }
  | { tipo: 'fraccion'; signo: string; numerador: number; denominador: number; texto: string }
  | { tipo: 'operador'; texto: string }
  | { tipo: 'casilla'; texto: string }
  | { tipo: 'texto'; texto: string };

const SIGNO_MENOS = /^[−-]/;
const RE_NUMERO = /^[−-]?\d+(?:[.,]\d+)?$/;
const RE_FRACCION = /^([−-]?)(\d+)\/(\d+)$/;
const RE_CASILLA = /^(?:_{2,}|□|\?)$/;
const OPERADORES = new Set(['+', '-', '−', '×', 'x', '*', '÷', ':', '/', '=', '(', ')']);

const LECTURA_OPERADOR: Record<string, string> = {
  '+': 'más',
  '-': 'menos',
  '−': 'menos',
  '×': 'por',
  x: 'por',
  '*': 'por',
  '÷': 'entre',
  ':': 'entre',
  '/': 'entre',
  '=': 'igual a',
  '(': 'abre paréntesis',
  ')': 'cierra paréntesis',
};

export function clasificar(termino: string): Token {
  const fraccion = RE_FRACCION.exec(termino);
  if (fraccion) {
    return {
      tipo: 'fraccion',
      signo: fraccion[1],
      numerador: Number(fraccion[2]),
      denominador: Number(fraccion[3]),
      texto: termino,
    };
  }
  if (RE_NUMERO.test(termino)) return { tipo: 'numero', texto: termino };
  if (RE_CASILLA.test(termino)) return { tipo: 'casilla', texto: termino };
  if (OPERADORES.has(termino)) return { tipo: 'operador', texto: termino };
  return { tipo: 'texto', texto: termino };
}

/** Divide una expresión en términos. Los espacios son el único separador. */
export function tokenizar(expresion: string): Token[] {
  return expresion.trim().split(/\s+/).filter(Boolean).map(clasificar);
}

function leerNumero(texto: string) {
  return SIGNO_MENOS.test(texto) ? `menos ${texto.slice(1)}` : texto;
}

/** Texto para lectores de pantalla, en el mismo orden que se ve. */
export function lecturaAccesible(tokens: Token[]): string {
  return tokens
    .map((t) => {
      switch (t.tipo) {
        case 'numero':
          return leerNumero(t.texto);
        case 'fraccion':
          return `${t.signo ? 'menos ' : ''}${t.numerador} sobre ${t.denominador}`;
        case 'operador':
          return LECTURA_OPERADOR[t.texto] ?? t.texto;
        case 'casilla':
          return 'término faltante';
        case 'texto':
          return t.texto;
      }
    })
    .join(' ');
}

export type FormatoOperacion = 'horizontal' | 'vertical';

/** Operación «a op b = c» con términos numéricos, apta para disposición vertical. */
export function operacionSimple(tokens: Token[]) {
  if (tokens.length !== 5) return null;
  const [a, op, b, igual, c] = tokens;
  const esCifra = (t: Token) => t.tipo === 'numero' || t.tipo === 'fraccion';
  if (!esCifra(a) || op.tipo !== 'operador' || !esCifra(b) || igual.texto !== '=' || !esCifra(c)) {
    return null;
  }
  return { a, operador: op.texto, b, c };
}

/** Fracción dentro de texto corrido, con la puntuación que la sigue. */
export const RE_FRACCION_EN_TEXTO = /^([−-]?\d+\/\d+)([.,;:?!)]*)$/;
