import { SIN_VALOR } from '../../../resultados/presentacion';
import type { ResultadoFactor } from '../../../resultados/tipos';
import type { FactorPerfil } from '../catalogo';
import { percentilVisible, textoPercentil, textoPuntuacionDirecta } from '../presentacionPerfil';
import { BarraPercentil } from './BarraPercentil';
import { EstadoFactor } from './EstadoFactor';

export interface TablaAreaProps {
  titulo: string;
  factores: FactorPerfil[];
  recibidos: Map<string, ResultadoFactor>;
}

/** Factores de un área del perfil: PD, percentil, marca en la escala y estado. */
export function TablaArea({ titulo, factores, recibidos }: TablaAreaProps) {
  const id = `perfil-area-${titulo.toLowerCase().replace(/\s+/g, '-')}`;
  return (
    <section className="perfil-area" aria-labelledby={id}>
      <h3 id={id}>{titulo}</h3>
      <div className="res-tabla-contenedor">
        <table className="res-tabla perfil-tabla">
          <caption className="visually-hidden">Factores del área {titulo}</caption>
          <thead>
            <tr>
              <th scope="col">Factor</th>
              <th scope="col">
                <abbr title="Puntuación directa">PD</abbr>
              </th>
              <th scope="col">Percentil</th>
              <th scope="col">Escala Pc 1–99</th>
              <th scope="col">Estado</th>
            </tr>
          </thead>
          <tbody>
            {factores.map(({ codigo, nombre, esTotal }) => {
              const f = recibidos.get(codigo);
              const calculado = f?.estadoCalculo === 'CALCULADO';
              return (
                <tr
                  key={codigo}
                  className={
                    [esTotal ? 'perfil-fila--total' : '', calculado ? '' : 'res-fila--sin-dato']
                      .join(' ')
                      .trim() || undefined
                  }
                >
                  <th scope="row">
                    <span className="perfil-nombre">{nombre}</span>
                    <span className="perfil-codigo">{codigo}</span>
                  </th>
                  <td className="res-numero perfil-pd">
                    {f ? textoPuntuacionDirecta(f) : SIN_VALOR}
                  </td>
                  <td className="res-numero perfil-pc">{f ? textoPercentil(f) : SIN_VALOR}</td>
                  <td className="perfil-celda-barra">
                    <BarraPercentil percentil={f ? percentilVisible(f) : null} />
                  </td>
                  <td>
                    <EstadoFactor factor={f} />
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </section>
  );
}
