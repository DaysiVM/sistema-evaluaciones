import { TestNotice } from '../../../components/TestNotice';
import { tieneRevisionPublicada } from '../presentacion';
import type { ResultadoResponse } from '../tipos';
import { AvisoEstadoResultado } from './AvisoEstadoResultado';
import { DatosRevision } from './DatosRevision';
import { TablaFactores, type TablaFactoresProps } from './TablaFactores';

export interface VistaResultadoProps extends Pick<TablaFactoresProps, 'etiquetaFactor'> {
  resultado: ResultadoResponse;
  /** Muestra un aviso cuando los datos vienen del mock. */
  datosSimulados?: boolean;
}

/**
 * Vista general y reutilizable de una respuesta de E9. No contiene reglas de un test:
 * presenta el estado, la revisión vigente y los factores tal como los devuelve el backend.
 */
export function VistaResultado({ resultado, datosSimulados, etiquetaFactor }: VistaResultadoProps) {
  const publicada = tieneRevisionPublicada(resultado);
  // Un resultado pendiente nunca se presenta como definitivo (Documento general §4.4).
  const mostrarFactores =
    resultado.estadoResultado === 'DISPONIBLE' && resultado.factores.length > 0;

  return (
    <section className="res-vista" aria-labelledby="res-titulo">
      <h2 id="res-titulo">Resultados de la evaluación {resultado.evaluacionId}</h2>
      {datosSimulados && (
        <TestNotice
          kind="warning"
          message="Datos simulados para desarrollo: no son puntuaciones ni percentiles reales."
        />
      )}
      <AvisoEstadoResultado resultado={resultado} />
      <DatosRevision resultado={resultado} />

      {mostrarFactores ? (
        <TablaFactores factores={resultado.factores} etiquetaFactor={etiquetaFactor} />
      ) : (
        <p className="res-vacio">
          {publicada && resultado.estadoResultado === 'DISPONIBLE'
            ? 'La revisión publicada no contiene factores.'
            : 'No hay una revisión publicada; no se muestran puntuaciones ni percentiles.'}
        </p>
      )}
    </section>
  );
}
