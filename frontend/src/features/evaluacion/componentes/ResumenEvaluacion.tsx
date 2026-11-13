import { useId } from 'react';
import { calcularProgreso, formatearFechaHora, formatearSegundos } from '../progreso';
import type {
  EstadoEvaluacion,
  EstadoSubprueba,
  EvaluacionResponse,
  PeriodoResponse,
  TestDetalleResponse,
} from '../tipos';

interface ResumenEvaluacionProps {
  /** Datos tal como los confirmó el servidor (E3 201 o E4 200). */
  evaluacion: EvaluacionResponse;
  test: TestDetalleResponse | null;
  periodos: PeriodoResponse[];
  titulo: string;
  onContinuar?: (evaluacion: EvaluacionResponse) => void;
}

const ESTADO_EVALUACION: Record<EstadoEvaluacion, string> = {
  EN_CURSO: 'En curso',
  FINALIZADA: 'Finalizada',
};

const ESTADO_SUBPRUEBA: Record<EstadoSubprueba, string> = {
  PENDIENTE: 'Pendiente',
  ACTIVA: 'Activa',
  CERRADA: 'Cerrada',
};

/** Estado, progreso y subpruebas de una evaluación; no modifica nada localmente. */
export function ResumenEvaluacion({
  evaluacion,
  test,
  periodos,
  titulo,
  onContinuar,
}: ResumenEvaluacionProps) {
  const id = useId();
  const { cerradas, total, porcentaje } = calcularProgreso(evaluacion);
  const nombre = (codigo: string) =>
    test?.subpruebas.find((s) => s.codigo === codigo)?.nombre ?? codigo;
  const periodo = periodos.find((p) => p.id === evaluacion.periodoId);
  const activa = evaluacion.subpruebaActiva;
  const siguiente = evaluacion.subpruebas.find((s) => s.estado === 'PENDIENTE');
  const enCurso = evaluacion.estado === 'EN_CURSO';

  return (
    <section className="ev-resumen" aria-labelledby={`${id}-titulo`}>
      <header className="ev-resumen-cabecera">
        <h3 id={`${id}-titulo`}>{titulo}</h3>
        <span className={`ev-estado ev-estado--${evaluacion.estado.toLowerCase()}`}>
          {ESTADO_EVALUACION[evaluacion.estado]}
        </span>
      </header>

      <dl className="ev-datos">
        <div>
          <dt>Evaluación</dt>
          <dd>#{evaluacion.id}</dd>
        </div>
        <div>
          <dt>Test</dt>
          <dd>{test && test.id === evaluacion.testId ? test.nombre : `ID ${evaluacion.testId}`}</dd>
        </div>
        <div>
          <dt>Evaluado</dt>
          <dd>ID {evaluacion.sujetoId}</dd>
        </div>
        <div>
          <dt>Período</dt>
          <dd>{periodo ? periodo.codigo : `ID ${evaluacion.periodoId}`}</dd>
        </div>
        <div>
          <dt>Inicio</dt>
          <dd>{formatearFechaHora(evaluacion.fechaInicio)}</dd>
        </div>
        {evaluacion.fechaFin && (
          <div>
            <dt>Fin</dt>
            <dd>{formatearFechaHora(evaluacion.fechaFin)}</dd>
          </div>
        )}
      </dl>

      <div className="ev-progreso">
        <label htmlFor={`${id}-progreso`}>
          Progreso: {cerradas} de {total} subpruebas cerradas ({porcentaje} %)
        </label>
        <progress id={`${id}-progreso`} max={100} value={porcentaje}>
          {porcentaje} %
        </progress>
      </div>

      {activa && (
        <p className="ev-activa">
          Subprueba activa: <strong>{nombre(activa.codigo)}</strong> · tiempo restante{' '}
          <span className="ev-reloj">{formatearSegundos(activa.segundosRestantes)}</span>{' '}
          <span className="ev-ayuda">(según el servidor)</span>
        </p>
      )}
      {!activa && enCurso && siguiente && (
        <p className="ev-activa">
          Siguiente subprueba: <strong>{nombre(siguiente.codigo)}</strong>
        </p>
      )}

      <ol className="ev-subpruebas">
        {evaluacion.subpruebas.map((s) => (
          <li key={s.codigo} className={`ev-sub ev-sub--${s.estado.toLowerCase()}`}>
            <span>{nombre(s.codigo)}</span>
            <span className="ev-sub-estado">{ESTADO_SUBPRUEBA[s.estado]}</span>
          </li>
        ))}
      </ol>

      {enCurso && onContinuar && (
        <div className="ev-acciones">
          <button type="button" className="ev-boton" onClick={() => onContinuar(evaluacion)}>
            Continuar con la evaluación
          </button>
        </div>
      )}
      {!enCurso && <p className="ev-ayuda">La evaluación ya finalizó; no admite más respuestas.</p>}
    </section>
  );
}
