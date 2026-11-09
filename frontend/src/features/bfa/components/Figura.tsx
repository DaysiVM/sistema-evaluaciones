export type FormaFigura = 'cuadrado' | 'triangulo' | 'circulo' | 'rombo' | 'cruz' | 'cubos';

type FiguraProps = {
  /** URL versionada del recurso (`recursoUrl` en la API). Sin ella se dibuja un marcador. */
  src?: string;
  alt: string;
  /** Forma del marcador; solo se usa mientras no exista el material real. */
  forma?: FormaFigura;
  /** `estimulo` es la figura del ítem; `opcion` es una alternativa. */
  rol?: 'estimulo' | 'opcion';
};

const formas: Record<FormaFigura, string> = {
  cuadrado: 'M40 20h40v40H40z',
  triangulo: 'M60 16l26 46H34z',
  circulo: 'M60 14a24 24 0 1 0 .01 0z',
  rombo: 'M60 14l26 24-26 24-26-24z',
  cruz: 'M52 14h16v18h18v16H68v18H52V48H34V32h18z',
  cubos: 'M60 14l24 13v26L60 66 36 53V27zM36 27l24 13 24-13M60 40v26',
};

/** Figura de un ítem: imagen real si hay `src`; si no, un marcador con la proporción 4:3. */
export function Figura({ src, alt, forma = 'cuadrado', rol = 'opcion' }: FiguraProps) {
  const className = `figura figura--${rol}`;

  if (src) {
    return <img className={className} src={src} alt={alt} draggable={false} />;
  }

  return (
    <svg
      className={`${className} figura--marcador`}
      viewBox="0 0 120 80"
      role="img"
      aria-label={alt}
    >
      <rect
        x="1"
        y="1"
        width="118"
        height="78"
        fill="none"
        stroke="currentColor"
        strokeDasharray="4 3"
      />
      <path
        d={formas[forma]}
        fill="none"
        stroke="currentColor"

        strokeLinejoin="round"
      />
    </svg>
  );
}
