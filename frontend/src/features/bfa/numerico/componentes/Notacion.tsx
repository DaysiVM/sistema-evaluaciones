import { Fragment, type ReactNode } from 'react';
import { Casilla, Fraccion, Operacion, OperacionVertical } from '../../components/Operacion';
import {
  RE_FRACCION_EN_TEXTO,
  clasificar,
  lecturaAccesible,
  operacionSimple,
  tokenizar,
  type FormatoOperacion,
  type Token,
} from '../notacion';

/** Elemento visual de un término. El signo de una fracción negativa queda fuera de la barra. */
function dibujar(t: Token, clave: number): ReactNode {
  switch (t.tipo) {
    case 'fraccion':
      return (
        <span className="num-fraccion" key={clave}>
          {t.signo}
          <Fraccion numerador={t.numerador} denominador={t.denominador} />
        </span>
      );
    case 'casilla':
      return <Casilla key={clave} />;
    default:
      return t.texto;
  }
}

type ExpresionProps = {
  expresion: string;
  /** `vertical` solo aplica a operaciones simples `a op b = c`; si no lo es, se dibuja en línea. */
  formato?: FormatoOperacion;
};

/** Expresión matemática (operación con resultado propuesto o ecuación incompleta). */
export function ExpresionMatematica({ expresion, formato = 'horizontal' }: ExpresionProps) {
  const tokens = tokenizar(expresion);
  const etiqueta = lecturaAccesible(tokens);
  const simple = formato === 'vertical' ? operacionSimple(tokens) : null;

  if (simple) {
    return (
      <OperacionVertical
        arriba={dibujar(simple.a, 0)}
        abajo={dibujar(simple.b, 1)}
        operador={simple.operador}
        resultado={dibujar(simple.c, 2)}
        etiqueta={etiqueta}
      />
    );
  }
  return <Operacion partes={tokens.map(dibujar)} etiqueta={etiqueta} />;
}

/**
 * Texto corrido (enunciado de un problema o alternativa) con fracciones apiladas.
 * Todo lo demás —cifras, decimales, horas, moneda, puntuación— se conserva literalmente.
 */
export function TextoMatematico({ texto }: { texto: string }) {
  const piezas = texto.split(/(\s+)/);
  return (
    <>
      {piezas.map((pieza, i) => {
        const m = RE_FRACCION_EN_TEXTO.exec(pieza);
        if (!m) return <Fragment key={i}>{pieza}</Fragment>;
        const token = clasificar(m[1]);
        return (
          <Fragment key={i}>
            <span className="visually-hidden">{lecturaAccesible([token])}</span>
            <span aria-hidden="true">{dibujar(token, i)}</span>
            {m[2]}
          </Fragment>
        );
      })}
    </>
  );
}
