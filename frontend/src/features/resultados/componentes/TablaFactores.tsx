import {
  describirEstadoCalculo,
  describirEstadoNormativo,
  SIN_VALOR,
  textoPuntuacion,
  valorVisible,
} from '../presentacion';
import type { ResultadoFactor } from '../tipos';
import { Insignia } from './Insignia';

export interface TablaFactoresProps {
  factores: ResultadoFactor[];
  /**
   * Nombre visible de un factor. Por defecto se muestra el código del backend; un módulo
   * de test (p. ej. BFA-FE-08) puede inyectar sus nombres sin cambiar esta vista.
   */
  etiquetaFactor?: (codigo: string) => string;
}

/** Factores en el orden recibido. Puntuaciones y percentiles solo si el backend los envía. */
export function TablaFactores({ factores, etiquetaFactor = (c) => c }: TablaFactoresProps) {
  return (
    <div className="res-tabla-contenedor">
      <table className="res-tabla">
        <caption className="visually-hidden">Resultados por factor</caption>
        <thead>
          <tr>
            <th scope="col">Factor</th>
            <th scope="col">Puntuación directa</th>
            <th scope="col">Cálculo</th>
            <th scope="col">Percentil</th>
            <th scope="col">Conversión</th>
            <th scope="col">Motivo</th>
          </tr>
        </thead>
        <tbody>
          {factores.map((f) => {
            const calculo = describirEstadoCalculo(f.estadoCalculo);
            const normativo = describirEstadoNormativo(f.estadoNormativo);
            return (
              <tr
                key={f.codigo}
                className={f.puntuacionDirecta === null ? 'res-fila--sin-dato' : undefined}
              >
                <th scope="row">{etiquetaFactor(f.codigo)}</th>
                <td className="res-numero">{textoPuntuacion(f)}</td>
                <td>
                  <Insignia texto={calculo.etiqueta} tono={calculo.tono} />
                </td>
                <td className="res-numero">{valorVisible(f.percentil)}</td>
                <td>
                  <Insignia texto={normativo.etiqueta} tono={normativo.tono} />
                </td>
                <td className="res-motivo">{f.motivo ?? SIN_VALOR}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
