import { SIN_VALOR } from '../../../resultados/presentacion';
import type { ResultadoFactor } from '../../../resultados/tipos';
import type { CompuestoPerfil } from '../catalogo';
import { percentilVisible, textoPercentil, textoPuntuacionDirecta } from '../presentacionPerfil';
import { BarraPercentil } from './BarraPercentil';
import { EstadoFactor } from './EstadoFactor';

/** Resultado compuesto (EIV, EIC, EIG, AE) tal como lo envía E9. La fórmula es solo informativa. */
export function TarjetaCompuesto({
  compuesto,
  factor,
}: {
  compuesto: CompuestoPerfil;
  factor: ResultadoFactor | undefined;
}) {
  const calculado = factor?.estadoCalculo === 'CALCULADO';
  return (
    <article
      className={`perfil-compuesto${calculado ? '' : ' perfil-compuesto--sin-dato'}`}
      aria-labelledby={`perfil-${compuesto.codigo}`}
    >
      <header>
        <h4 id={`perfil-${compuesto.codigo}`}>
          {compuesto.nombre} <span className="perfil-codigo">{compuesto.codigo}</span>
        </h4>
        <p className="perfil-formula">
          {compuesto.codigo} = {compuesto.formula}
        </p>
      </header>
      <dl className="perfil-valores">
        <div>
          <dt>
            <abbr title="Puntuación directa">PD</abbr>
          </dt>
          <dd className="res-numero perfil-pd">
            {factor ? textoPuntuacionDirecta(factor) : SIN_VALOR}
          </dd>
        </div>
        <div>
          <dt>Percentil</dt>
          <dd className="res-numero perfil-pc">{factor ? textoPercentil(factor) : SIN_VALOR}</dd>
        </div>
      </dl>
      <BarraPercentil percentil={factor ? percentilVisible(factor) : null} />
      <EstadoFactor factor={factor} />
    </article>
  );
}
