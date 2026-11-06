import { useState } from 'react';
import { TestScreen, type TestPhase } from '../components/TestScreen';
import type { SaveState } from '../components/SaveStatus';
import { FluidezVerbal } from '../features/bfa/components/FluidezVerbal';
import { subpruebas, type FamiliaSubprueba } from './ui04-subpruebas';

const fases = ['instructions', 'practice', 'active', 'closed'] as const;
const nombresFase: Record<TestPhase, string> = {
  instructions: 'Instrucciones',
  practice: 'Práctica',
  active: 'Pregunta',
  closed: 'Cerrada',
};
const guardados = ['saved', 'saving', 'error'] as const;
const nombresGuardado: Record<SaveState, string> = {
  saved: 'Guardado',
  saving: 'Guardando',
  error: 'Error de guardado',
};
const avisos = ['none', 'warning', 'error'] as const;
type TipoAviso = (typeof avisos)[number];
const nombresAviso: Record<TipoAviso, string> = {
  none: 'Sin aviso',
  warning: 'Aviso',
  error: 'Error',
};
const familias: FamiliaSubprueba[] = [
  'Vocabulario',
  'Espacial',
  'Razonamiento',
  'Comprensión verbal',
  'Numérico',
  'Fluidez verbal',
];

function leerParametro<T extends string>(nombre: string, permitidos: readonly T[], base: T): T {
  const valor = new URLSearchParams(window.location.search).get(nombre);
  return permitidos.includes(valor as T) ? (valor as T) : base;
}

function formatearTiempo(segundos: number) {
  return `${Math.floor(segundos / 60)}:${String(segundos % 60).padStart(2, '0')}`;
}

function avisoDeEjemplo(tipo: TipoAviso) {
  if (tipo === 'warning')
    return { kind: 'warning' as const, message: 'Aviso de tiempo de ejemplo' };
  if (tipo === 'error') return { kind: 'error' as const, message: 'Error de ejemplo' };
  return undefined;
}

/** Vista previa de UI-04: una variante visual por cada subprueba del BFA. Solo contenido de ejemplo. */
export function Ui04Preview() {
  const codigos = subpruebas.map((s) => s.codigo);
  const [codigo, setCodigo] = useState(() => leerParametro('subprueba', codigos, codigos[0]));
  const [fase, setFase] = useState<TestPhase>(() => leerParametro('phase', fases, 'active'));
  const [guardado, setGuardado] = useState<SaveState>(() =>
    leerParametro('save', guardados, 'saved'),
  );
  const [aviso, setAviso] = useState<TipoAviso>(() => leerParametro('notice', avisos, 'none'));
  const [indice, setIndice] = useState(0);
  const [seleccion, setSeleccion] = useState<string | null>(null);
  const [palabras, setPalabras] = useState<string[]>([]);

  const subprueba = subpruebas.find((s) => s.codigo === codigo) ?? subpruebas[0];
  const item =
    subprueba.items.length > 0 ? subprueba.items[indice % subprueba.items.length] : undefined;

  const elegir = (nuevo: string) => {
    setCodigo(nuevo);
    setIndice(0);
    setSeleccion(null);
  };
  const siguiente = () => {
    setIndice((actual) => actual + 1);
    setSeleccion(null);
  };
  const anterior = () => {
    setIndice((actual) => Math.max(0, actual - 1));
    setSeleccion(null);
  };

  const ficha: [string, string][] = [
    ['Contenido', subprueba.contenido],
    ['Respuesta', subprueba.respuesta],
    ['Tiempo oficial', formatearTiempo(subprueba.tiempoSegundos)],
    ['Requisito', subprueba.requisito],
    [
      'Volver atrás',
      subprueba.volverAtras === null ? 'Por confirmar' : subprueba.volverAtras ? 'Sí' : 'No',
    ],
  ];
  if (subprueba.pendiente) ficha.push(['Pendiente', subprueba.pendiente]);

  return (
    <main className="app-shell">
      <section className="preview-toolbar" aria-labelledby="preview-title">
        <div className="preview-toolbar-heading">
          <p className="preview-eyebrow">UI-04 · herramienta de preview</p>
          <h1 id="preview-title">Variantes visuales del BFA</h1>
          <p>
            Una variante por subprueba, con contenido de ejemplo. Las figuras son marcadores hasta
            contar con el material oficial. Estos controles son temporales.
          </p>
        </div>

        <div className="preview-control-grid preview-control-grid--subpruebas">
          {familias.map((familia) => (
            <fieldset className="preview-control-group" key={familia}>
              <legend>{familia}</legend>
              <div className="preview-choice-list">
                {subpruebas
                  .filter((s) => s.familia === familia)
                  .map((s) => (
                    <button
                      key={s.codigo}
                      className="preview-choice"
                      type="button"
                      aria-pressed={codigo === s.codigo}
                      onClick={() => elegir(s.codigo)}
                    >
                      {s.nombre.split(' · ')[1] ?? s.nombre}
                    </button>
                  ))}
              </div>
            </fieldset>
          ))}
        </div>

        <div className="preview-control-grid preview-control-grid--estado">
          <fieldset className="preview-control-group">
            <legend>Fase</legend>
            <div className="preview-choice-list">
              {fases.map((opcion) => (
                <button
                  key={opcion}
                  className="preview-choice"
                  type="button"
                  aria-pressed={fase === opcion}
                  onClick={() => setFase(opcion)}
                >
                  {nombresFase[opcion]}
                </button>
              ))}
            </div>
          </fieldset>
          <fieldset className="preview-control-group">
            <legend>Guardado</legend>
            <div className="preview-choice-list">
              {guardados.map((opcion) => (
                <button
                  key={opcion}
                  className="preview-choice"
                  type="button"
                  aria-pressed={guardado === opcion}
                  onClick={() => setGuardado(opcion)}
                >
                  {nombresGuardado[opcion]}
                </button>
              ))}
            </div>
          </fieldset>
          <fieldset className="preview-control-group">
            <legend>Aviso</legend>
            <div className="preview-choice-list">
              {avisos.map((opcion) => (
                <button
                  key={opcion}
                  className="preview-choice"
                  type="button"
                  aria-pressed={aviso === opcion}
                  onClick={() => setAviso(opcion)}
                >
                  {nombresAviso[opcion]}
                </button>
              ))}
            </div>
          </fieldset>
        </div>
      </section>

      <section className="preview-spec" aria-label={`Ficha de ${subprueba.nombre}`}>
        <h2>
          {subprueba.nombre} <span>({subprueba.codigo})</span>
        </h2>
        <dl>
          {ficha.map(([termino, detalle]) => (
            <div key={termino}>
              <dt>{termino}</dt>
              <dd>{detalle}</dd>
            </div>
          ))}
        </dl>
      </section>

      <div className="test-stage">
        <TestScreen
          testName={subprueba.nombre}
          questionNumber={indice + 1}
          totalQuestions={0}
          questionLabel={subprueba.etiqueta(indice + 1)}
          timeRemainingSeconds={fase === 'active' ? subprueba.tiempoSegundos : null}
          prompt={item ? item.prompt : 'Escribe palabras con la letra asignada'}
          promptVariant={subprueba.promptVariant}
          optionsLayout={subprueba.optionsLayout}
          numeric={subprueba.numeric}
          options={item?.options}
          questionResource={item?.recurso}
          selectedOption={seleccion}
          responseArea={
            subprueba.fluidez && (
              <FluidezVerbal
                letra={subprueba.fluidez.letra}
                maxEntradas={subprueba.fluidez.maxEntradas}
                palabras={palabras}
                onChange={setPalabras}
              />
            )
          }
          saveState={guardado}
          notice={avisoDeEjemplo(aviso)}
          phase={fase}
          onSelect={setSeleccion}
          onPrevious={subprueba.volverAtras && indice > 0 ? anterior : undefined}
          onNext={siguiente}
        />
      </div>
    </main>
  );
}
