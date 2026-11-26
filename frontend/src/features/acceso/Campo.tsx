import { useId, type InputHTMLAttributes } from 'react';

/** Campo de formulario con etiqueta, usando las clases del módulo de evaluación. */
export function Campo({
  etiqueta,
  ...entrada
}: { etiqueta: string } & InputHTMLAttributes<HTMLInputElement>) {
  const id = useId();
  return (
    <div className="ev-campo">
      <label htmlFor={id}>{etiqueta}</label>
      <input id={id} className="ev-input" {...entrada} />
    </div>
  );
}
