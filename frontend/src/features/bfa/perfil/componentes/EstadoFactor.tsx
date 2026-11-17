import { describirEstadoCalculo, describirEstadoNormativo } from '../../../resultados/presentacion';
import type { ResultadoFactor } from '../../../resultados/tipos';
import { Insignia } from '../../../resultados/componentes/Insignia';

/** Estado de cálculo, estado normativo (solo si está calculado) y motivo recibido. */
export function EstadoFactor({ factor }: { factor: ResultadoFactor | undefined }) {
  if (!factor) {
    return (
      <div className="perfil-estado">
        <Insignia texto="No recibido" tono="neutro" />
        <p className="res-motivo">El servidor no envió este factor.</p>
      </div>
    );
  }
  const calculo = describirEstadoCalculo(factor.estadoCalculo);
  const normativo = describirEstadoNormativo(factor.estadoNormativo);
  return (
    <div className="perfil-estado">
      <span className="perfil-insignias">
        <Insignia texto={calculo.etiqueta} tono={calculo.tono} />
        {factor.estadoCalculo === 'CALCULADO' && (
          <Insignia texto={normativo.etiqueta} tono={normativo.tono} />
        )}
      </span>
      {factor.motivo && <p className="res-motivo">{factor.motivo}</p>}
    </div>
  );
}
