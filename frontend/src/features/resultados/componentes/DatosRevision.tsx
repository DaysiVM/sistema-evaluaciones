import { valorVisible } from '../presentacion';
import type { ResultadoResponse } from '../tipos';

/**
 * Identificadores de la revisión y de los insumos usados (trazabilidad).
 * Son solo IDs o etiquetas de versión: nunca se muestra el contenido de la clave.
 */
export function DatosRevision({ resultado }: { resultado: ResultadoResponse }) {
  const filas: [string, string][] = [
    ['Evaluación', valorVisible(resultado.evaluacionId)],
    ['Resultado', valorVisible(resultado.resultadoId)],
    ['Revisión vigente', valorVisible(resultado.revision)],
    ['Calificación', valorVisible(resultado.calificacionId)],
    ['Versión del formulario', valorVisible(resultado.versionFormularioId)],
    ['Versión de reglas', valorVisible(resultado.versionReglas)],
    ['Clave', valorVisible(resultado.idClave)],
    ['Baremo', valorVisible(resultado.idBaremo)],
  ];
  return (
    <dl className="res-datos">
      {filas.map(([nombre, valor]) => (
        <div key={nombre}>
          <dt>{nombre}</dt>
          <dd>{valor}</dd>
        </div>
      ))}
    </dl>
  );
}
