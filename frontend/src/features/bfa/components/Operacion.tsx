import type { ReactNode } from 'react';

/** Operación en una línea: cada parte (número, operador, "=", casilla) va como un elemento. */
export function Operacion({ partes, etiqueta }: { partes: ReactNode[]; etiqueta: string }) {
  return (
    <span className="operacion" role="img" aria-label={etiqueta}>
      {partes.map((parte, indice) => (
        <span key={indice} aria-hidden="true">
          {parte}
        </span>
      ))}
    </span>
  );
}

type OperacionVerticalProps = {
  arriba: ReactNode;
  abajo: ReactNode;
  operador: string;
  resultado: ReactNode;
  etiqueta: string;
};

/** Operación en columna: cifras alineadas a la derecha, signo a la izquierda y raya antes del resultado. */
export function OperacionVertical({
  arriba,
  abajo,
  operador,
  resultado,
  etiqueta,
}: OperacionVerticalProps) {
  return (
    <span className="operacion-vertical" role="img" aria-label={etiqueta}>
      <span className="operacion-fila" aria-hidden="true">
        {arriba}
      </span>
      <span className="operacion-fila" aria-hidden="true">
        <span className="operacion-signo">{operador}</span>
        {abajo}
      </span>
      <span className="operacion-raya" aria-hidden="true" />
      <span className="operacion-fila" aria-hidden="true">
        {resultado}
      </span>
    </span>
  );
}

/** Fracción apilada; nunca se escribe como "a/b" para no cambiar la notación del material. */
export function Fraccion({ numerador, denominador }: { numerador: number; denominador: number }) {
  return (
    <span className="fraccion">
      <span>{numerador}</span>
      <span>{denominador}</span>
    </span>
  );
}

/** Espacio en blanco de una ecuación incompleta. */
export function Casilla() {
  return <span className="casilla" />;
}
