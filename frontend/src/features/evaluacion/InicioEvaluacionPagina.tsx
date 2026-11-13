import { useId, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router';
import { EstadoCarga } from './componentes/EstadoCarga';
import { FormularioInicio } from './componentes/FormularioInicio';
import { MensajeError } from './componentes/MensajeError';
import { ResumenEvaluacion } from './componentes/ResumenEvaluacion';
import { RetomarEvaluacion } from './componentes/RetomarEvaluacion';
import { useOpcionesInicio } from './hooks/useOpcionesInicio';
import type { EvaluacionResponse } from './tipos';
import { validarId } from './validaciones';
import './evaluacion.css';

type Pestana = 'iniciar' | 'retomar';

/** Test por defecto mientras el catálogo (GEN-01) no envía ?testId. 1 = BFA. */
const TEST_POR_DEFECTO = '1';

/**
 * GEN-02-FE — Inicio y recuperación de una evaluación.
 * Ruta: /evaluaciones/inicio?testId=<id>
 */
export function InicioEvaluacionPagina() {
  const [parametros] = useSearchParams();
  const textoTest = parametros.get('testId') ?? TEST_POR_DEFECTO;
  const errorTest = validarId(textoTest, 'del test');

  return (
    <div className="ev-pagina">
      <header className="ev-pagina-cabecera">
        <h1>Evaluación</h1>
        <p>Inicie una evaluación nueva o retome una existente sin reiniciar su tiempo.</p>
      </header>
      {errorTest ? (
        <MensajeError
          error={{
            tipo: 'validacion',
            codigo: 'TEST_INVALIDO',
            mensaje: `${errorTest} Vuelva al catálogo y seleccione un test.`,
            detalles: [],
          }}
        />
      ) : (
        <ContenidoEvaluacion testId={Number(textoTest)} />
      )}
    </div>
  );
}

function ContenidoEvaluacion({ testId }: { testId: number }) {
  const id = useId();
  const navegar = useNavigate();
  const { estado, reintentar } = useOpcionesInicio(testId);
  const [pestana, setPestana] = useState<Pestana>('iniciar');
  // Solo se guarda lo que el servidor confirmó con 201; nunca un borrador local.
  const [creada, setCreada] = useState<EvaluacionResponse | null>(null);

  const opciones = estado.fase === 'exito' ? estado.datos : null;
  const continuar = (evaluacion: EvaluacionResponse) => {
    void navegar(`/evaluaciones/${evaluacion.id}`);
  };

  const pestanaId = (p: Pestana) => `${id}-pestana-${p}`;
  const panelId = (p: Pestana) => `${id}-panel-${p}`;

  return (
    <>
      <div className="ev-pestanas" role="tablist" aria-label="Acción">
        {(['iniciar', 'retomar'] as const).map((p) => (
          <button
            key={p}
            id={pestanaId(p)}
            type="button"
            role="tab"
            className="ev-pestana"
            aria-selected={pestana === p}
            aria-controls={panelId(p)}
            onClick={() => setPestana(p)}
          >
            {p === 'iniciar' ? 'Iniciar nueva' : 'Retomar'}
          </button>
        ))}
      </div>

      <section
        id={panelId(pestana)}
        role="tabpanel"
        aria-labelledby={pestanaId(pestana)}
        className="ev-panel"
      >
        {pestana === 'retomar' ? (
          <RetomarEvaluacion
            test={opciones?.test ?? null}
            periodos={opciones?.periodos ?? []}
            onContinuar={continuar}
          />
        ) : estado.fase === 'error' ? (
          <MensajeError error={estado.error} onReintentar={() => void reintentar()} />
        ) : !opciones ? (
          <EstadoCarga texto="Cargando configuraciones del test…" />
        ) : creada ? (
          <div className="ev-retomar">
            <ResumenEvaluacion
              evaluacion={creada}
              test={opciones.test}
              periodos={opciones.periodos}
              titulo="Evaluación creada"
              onContinuar={continuar}
            />
            <div className="ev-acciones">
              <button
                type="button"
                className="ev-boton ev-boton--secundario"
                onClick={() => setCreada(null)}
              >
                Iniciar otra evaluación
              </button>
            </div>
          </div>
        ) : (
          <FormularioInicio
            test={opciones.test}
            periodos={opciones.periodos}
            onIniciada={setCreada}
          />
        )}
      </section>
    </>
  );
}
