import { useParams } from 'react-router';
import '../evaluacion/evaluacion.css';
import { ConsultaResultados } from './componentes/ConsultaResultados';
import './resultados.css';

/** GEN-05-FE: consulta general de resultados (E9). Sirve para cualquier test. */
export function ResultadosPagina() {
  const { evaluacionId } = useParams();
  return (
    <div className="ev-pagina">
      <header className="ev-pagina-cabecera">
        <h1>Resultados de evaluación</h1>
        <p>Consulte el estado y la revisión vigente publicada de una evaluación finalizada.</p>
      </header>
      <div className="ev-panel">
        {/* key: si cambia el ID de la ruta se reinicia la consulta. */}
        <ConsultaResultados key={evaluacionId ?? ''} evaluacionIdInicial={evaluacionId} />
      </div>
    </div>
  );
}
