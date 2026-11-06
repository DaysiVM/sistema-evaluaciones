import { CODIGOS_NUMERICO, CONFIG_NUMERICO, type CodigoNumerico } from '../subpruebas';
import '../numerico.css';

export type ResumenSubprueba = { respondidas: number; total: number };

type FinFamiliaProps = {
  /** Respuestas registradas por subprueba; nunca aciertos ni puntuaciones. */
  resumen: Partial<Record<CodigoNumerico, ResumenSubprueba>>;
  /** Lo decide el flujo BFA (en la batería sigue Fluidez Verbal). */
  onContinuar?: () => void;
};

/** Cierre de la sección numérica: confirma lo registrado sin mostrar resultados. */
export function FinFamilia({ resumen, onContinuar }: FinFamiliaProps) {
  return (
    <section className="test-paper num-fin" aria-labelledby="num-fin-titulo">
      <header className="test-header">
        <span className="test-name">Sección numérica</span>
      </header>
      <div className="test-content">
        <p className="phase-label">Terminada</p>
        <h1 className="question-prompt" id="num-fin-titulo">
          Sección numérica terminada
        </h1>
        <p className="phase-description">
          Las cuatro subpruebas se cerraron y sus respuestas quedaron registradas. Los resultados no
          se muestran durante la aplicación. Espera las indicaciones del aplicador para continuar
          con la siguiente prueba.
        </p>
        <table className="num-fin-tabla">
          <caption className="visually-hidden">Respuestas registradas por subprueba</caption>
          <thead>
            <tr>
              <th scope="col">Subprueba</th>
              <th scope="col">Respuestas registradas</th>
            </tr>
          </thead>
          <tbody>
            {CODIGOS_NUMERICO.map((codigo) => {
              const fila = resumen[codigo];
              return (
                <tr key={codigo}>
                  <th scope="row">
                    {codigo} · {CONFIG_NUMERICO[codigo].nombre.replace('Numérico · ', '')}
                  </th>
                  <td>{fila ? `${fila.respondidas} de ${fila.total}` : '—'}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
      {onContinuar && (
        <footer className="test-footer">
          <span />
          <div className="test-actions">
            <button className="primary-action" type="button" onClick={onContinuar}>
              Continuar
            </button>
          </div>
        </footer>
      )}
    </section>
  );
}
