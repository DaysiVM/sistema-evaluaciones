import { TestNotice } from '../../../../components/TestNotice';
import { AvisoEstadoResultado } from '../../../resultados/componentes/AvisoEstadoResultado';
import { DatosRevision } from '../../../resultados/componentes/DatosRevision';
import type { ResultadoResponse } from '../../../resultados/tipos';
import { AREAS, CODIGOS_PERFIL, COMPUESTOS, nombreFactor } from '../catalogo';
import { resumirPerfil } from '../presentacionPerfil';
import { TablaArea } from './TablaArea';
import { TarjetaCompuesto } from './TarjetaCompuesto';
import { Insignia } from '../../../resultados/componentes/Insignia';

export interface PerfilBfaProps {
  resultado: ResultadoResponse;
  /** Muestra el aviso de datos simulados (mock o vista previa). */
  datosSimulados?: boolean;
}

/**
 * Perfil de resultados BFA (diseño UI-05). Presenta por área y en compuestos lo que
 * devuelve E9, sin calcular ni completar. Sin revisión publicada no muestra valores.
 */
export function PerfilBfa({ resultado, datosSimulados }: PerfilBfaProps) {
  const recibidos = new Map(resultado.factores.map((f) => [f.codigo, f]));
  const mostrar = resultado.estadoResultado === 'DISPONIBLE' && resultado.factores.length > 0;
  const resumen = resumirPerfil(resultado.factores);
  // Códigos que no están en el catálogo: se muestran aparte para no ocultar datos recibidos.
  const otros = resultado.factores
    .filter((f) => !CODIGOS_PERFIL.includes(f.codigo))
    .map((f) => ({ codigo: f.codigo, nombre: nombreFactor(f.codigo) }));

  return (
    <section className="res-vista perfil" aria-labelledby="perfil-titulo">
      <h2 id="perfil-titulo">Perfil de resultados BFA · Evaluación {resultado.evaluacionId}</h2>
      {datosSimulados && (
        <TestNotice
          kind="warning"
          message="Datos simulados para revisar el diseño: no son puntuaciones ni percentiles reales."
        />
      )}
      <AvisoEstadoResultado resultado={resultado} />
      <DatosRevision resultado={resultado} />

      {mostrar ? (
        <>
          <div className="perfil-resumen" role="status">
            <Insignia
              texto={resumen.parcial ? 'Perfil parcial' : 'Perfil completo'}
              tono={resumen.parcial ? 'aviso' : 'exito'}
            />
            <p>
              {resumen.calculados} de {resumen.total} calculados · {resumen.conPercentil} con
              percentil
            </p>
          </div>

          <div className="perfil-leyenda">
            <p>
              <strong>PD</strong>: puntuación directa, antes de convertir. <strong>Pc</strong>:
              percentil según el baremo identificado arriba; no es un porcentaje de aciertos.
            </p>
            <p>
              La marca en la escala solo aparece cuando el factor tiene un percentil convertido. «—»
              indica que el servidor no envió el valor.
            </p>
          </div>

          <div className="perfil-areas">
            {AREAS.map((a) => (
              <TablaArea
                key={a.area}
                titulo={a.titulo}
                factores={a.factores}
                recibidos={recibidos}
              />
            ))}
          </div>

          <section className="perfil-seccion-compuestos" aria-labelledby="perfil-compuestos">
            <h3 id="perfil-compuestos">Resultados compuestos</h3>
            <div className="perfil-compuestos">
              {COMPUESTOS.map((c) => (
                <TarjetaCompuesto key={c.codigo} compuesto={c} factor={recibidos.get(c.codigo)} />
              ))}
            </div>
          </section>

          {otros.length > 0 && (
            <TablaArea titulo="Otros códigos recibidos" factores={otros} recibidos={recibidos} />
          )}
        </>
      ) : (
        <p className="res-vacio">
          No hay una revisión publicada; no se muestran puntuaciones ni percentiles.
        </p>
      )}
    </section>
  );
}
