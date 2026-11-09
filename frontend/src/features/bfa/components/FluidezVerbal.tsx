import { useRef } from 'react';

type FluidezVerbalProps = {
  /** Letra de la aplicación; la define el evaluador, no es una constante de la prueba. */
  letra: string;
  palabras: string[];
  maxEntradas?: number;
  onChange: (palabras: string[]) => void;
};

/** Grilla de entradas de Fluidez Verbal: Enter pasa a la siguiente y la letra siempre se ve. */
export function FluidezVerbal({ letra, palabras, maxEntradas = 75, onChange }: FluidezVerbalProps) {
  const entradas = useRef<(HTMLInputElement | null)[]>([]);
  const escritas = palabras.filter((palabra) => palabra.trim() !== '').length;

  const cambiar = (indice: number, valor: string) => {
    const siguiente = Array.from({ length: maxEntradas }, (_, i) => palabras[i] ?? '');
    siguiente[indice] = valor;
    onChange(siguiente);
  };

  return (
    <div className="fluidez">
      <div className="fluidez-cabecera">
        <span className="fluidez-letra" aria-hidden="true">
          {letra}
        </span>
        <div>
          <p className="fluidez-titulo">Letra asignada: {letra}</p>
          <p className="fluidez-conteo">
            {escritas} de {maxEntradas} palabras escritas
          </p>
        </div>
      </div>
      <ol className="fluidez-lista" aria-label="Palabras escritas">
        {Array.from({ length: maxEntradas }, (_, indice) => (
          <li key={indice}>
            <label className="fluidez-entrada">
              <span className="fluidez-numero" aria-hidden="true">
                {indice + 1}
              </span>
              <input
                ref={(elemento) => {
                  entradas.current[indice] = elemento;
                }}
                type="text"
                lang="es"
                aria-label={`Palabra ${indice + 1}`}
                value={palabras[indice] ?? ''}
                autoComplete="off"
                autoCorrect="off"
                autoCapitalize="none"
                spellCheck={false}
                onChange={(evento) => cambiar(indice, evento.target.value)}
                onKeyDown={(evento) => {
                  if (evento.key !== 'Enter') return;
                  evento.preventDefault();
                  entradas.current[indice + 1]?.focus();
                }}
              />
            </label>
          </li>
        ))}
      </ol>
    </div>
  );
}
