import { useState } from 'react';
import { PerfilBfa } from '../features/bfa/perfil/componentes/PerfilBfa';
import '../features/evaluacion/evaluacion.css';
import '../features/resultados/resultados.css';
import '../features/bfa/perfil/perfil.css';
import { ESCENARIOS_UI05 } from './ui05-escenarios';

function escenarioInicial(): string {
  const pedido = new URLSearchParams(window.location.search).get('escenario');
  return ESCENARIOS_UI05.some((e) => e.id === pedido) ? (pedido as string) : 'completo';
}

/** Vista previa del diseño UI-05: perfil de resultados BFA con escenarios simulados. */
export function Ui05Preview() {
  const [id, setId] = useState(escenarioInicial);
  const escenario = ESCENARIOS_UI05.find((e) => e.id === id) ?? ESCENARIOS_UI05[0];

  return (
    <main className="app-shell">
      <section className="preview-toolbar" aria-labelledby="preview-title">
        <div className="preview-toolbar-heading">
          <p className="preview-eyebrow">UI-05 · herramienta de preview</p>
          <h1 id="preview-title">Perfil de resultados BFA</h1>
          <p>{escenario.descripcion}</p>
        </div>
        <div className="preview-control-grid">
          <fieldset className="preview-control-group">
            <legend>Escenario</legend>
            <div className="preview-choice-list">
              {ESCENARIOS_UI05.map((e) => (
                <button
                  key={e.id}
                  type="button"
                  className="preview-choice"
                  aria-pressed={e.id === id}
                  onClick={() => setId(e.id)}
                >
                  {e.titulo}
                </button>
              ))}
            </div>
          </fieldset>
        </div>
      </section>
      <div className="ev-pagina">
        <div className="ev-panel">
          <PerfilBfa resultado={escenario.resultado} datosSimulados />
        </div>
      </div>
    </main>
  );
}
