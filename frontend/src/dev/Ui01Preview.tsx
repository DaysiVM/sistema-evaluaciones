import { useState } from 'react';
import { TestScreen, type TestPhase, type TestResponseType } from '../components/TestScreen';

const demoOptions = [
  { id: 'a', label: 'Opción de ejemplo A' },
  { id: 'b', label: 'Opción de ejemplo B' },
  { id: 'c', label: 'Opción de ejemplo C' },
  { id: 'd', label: 'Opción de ejemplo D' },
  { id: 'e', label: 'Opción de ejemplo E' },
];

const previewSaveStates = ['saved', 'saving', 'error'] as const;
type PreviewSaveState = (typeof previewSaveStates)[number];
const previewPhases = ['instructions', 'practice', 'active', 'closed'] as const;
const previewNoticeKinds = ['none', 'warning', 'error'] as const;
type PreviewNoticeKind = (typeof previewNoticeKinds)[number];

const phaseLabels: Record<TestPhase, string> = {
  instructions: 'Instrucciones',
  practice: 'Práctica',
  active: 'Pregunta',
  closed: 'Cerrada',
};

const responseLabels: Record<TestResponseType, string> = {
  selection: 'Selección',
  text: 'Texto libre',
};

const saveLabels: Record<PreviewSaveState, string> = {
  saved: 'Guardado',
  saving: 'Guardando',
  error: 'Error de guardado',
};

function getSearchParams() {
  return new URLSearchParams(window.location.search);
}

function getPreviewSaveState(): PreviewSaveState {
  const saveState = getSearchParams().get('save');

  return previewSaveStates.includes(saveState as PreviewSaveState)
    ? (saveState as PreviewSaveState)
    : 'saved';
}

function getPreviewNoticeKind(): PreviewNoticeKind {
  const notice = getSearchParams().get('notice');

  return previewNoticeKinds.includes(notice as PreviewNoticeKind)
    ? (notice as PreviewNoticeKind)
    : 'none';
}

function getPreviewNotice(kind: PreviewNoticeKind) {
  if (kind === 'warning') {
    return { kind: 'warning' as const, message: 'Aviso de tiempo de ejemplo' };
  }

  if (kind === 'error') {
    return { kind: 'error' as const, message: 'Error de ejemplo' };
  }

  return undefined;
}

function getPreviewPhase(): TestPhase {
  const phase = getSearchParams().get('phase');

  return previewPhases.includes(phase as TestPhase) ? (phase as TestPhase) : 'active';
}

function getPreviewResponseType(): TestResponseType {
  return getSearchParams().get('response') === 'text' ? 'text' : 'selection';
}

function getPreviewResource(showResource: boolean) {
  if (!showResource) return undefined;

  return (
    <div
      className="question-resource-placeholder"
      role="img"
      aria-label="Recurso visual de ejemplo"
    >
      Recurso visual de ejemplo
    </div>
  );
}

function getPreviewActionLabel(phase: TestPhase) {
  if (phase === 'instructions') return 'Comenzar';
  if (phase === 'practice') return 'Continuar';
  if (phase === 'closed') return 'Volver';
  return 'Siguiente';
}

export function Ui01Preview() {
  const [selectedOption, setSelectedOption] = useState<string | null>('b');
  const [textResponse, setTextResponse] = useState('');
  const [questionNumber, setQuestionNumber] = useState(1);
  const [phase, setPhase] = useState<TestPhase>(getPreviewPhase);
  const [responseType, setResponseType] = useState<TestResponseType>(getPreviewResponseType);
  const [saveState, setSaveState] = useState<PreviewSaveState>(getPreviewSaveState);
  const [noticeKind, setNoticeKind] = useState<PreviewNoticeKind>(getPreviewNoticeKind);
  const [showResource, setShowResource] = useState(
    () => getSearchParams().get('resource') === 'visual',
  );

  const handleNext = () => {
    setQuestionNumber((current) => (current === 3 ? 1 : current + 1));
    setSelectedOption(null);
    setTextResponse('');
  };

  return (
    <main className="app-shell">
      <section className="preview-toolbar" aria-labelledby="preview-title">
        <div className="preview-toolbar-heading">
          <p className="preview-eyebrow">UI-01 · herramienta de preview</p>
          <h1 id="preview-title">Variaciones del test</h1>
          <p>
            Estos controles son temporales y solo sirven para revisar la biblioteca visual antes de
            conectarla al flujo real de evaluación.
          </p>
        </div>

        <div className="preview-control-grid">
          <fieldset className="preview-control-group">
            <legend>Fase</legend>
            <div className="preview-choice-list">
              {previewPhases.map((option) => (
                <button
                  key={option}
                  className="preview-choice"
                  type="button"
                  aria-pressed={phase === option}
                  onClick={() => setPhase(option)}
                >
                  {phaseLabels[option]}
                </button>
              ))}
            </div>
          </fieldset>

          <fieldset className="preview-control-group">
            <legend>Respuesta</legend>
            <div className="preview-choice-list">
              {(Object.keys(responseLabels) as TestResponseType[]).map((option) => (
                <button
                  key={option}
                  className="preview-choice"
                  type="button"
                  aria-pressed={responseType === option}
                  onClick={() => setResponseType(option)}
                >
                  {responseLabels[option]}
                </button>
              ))}
            </div>
          </fieldset>

          <fieldset className="preview-control-group">
            <legend>Guardado</legend>
            <div className="preview-choice-list">
              {previewSaveStates.map((option) => (
                <button
                  key={option}
                  className="preview-choice"
                  type="button"
                  aria-pressed={saveState === option}
                  onClick={() => setSaveState(option)}
                >
                  {saveLabels[option]}
                </button>
              ))}
            </div>
          </fieldset>

          <fieldset className="preview-control-group">
            <legend>Aviso</legend>
            <div className="preview-choice-list">
              {previewNoticeKinds.map((option) => (
                <button
                  key={option}
                  className="preview-choice"
                  type="button"
                  aria-pressed={noticeKind === option}
                  onClick={() => setNoticeKind(option)}
                >
                  {option === 'none' ? 'Sin aviso' : option === 'warning' ? 'Aviso' : 'Error'}
                </button>
              ))}
            </div>
          </fieldset>

          <fieldset className="preview-control-group">
            <legend>Recurso</legend>
            <div className="preview-choice-list">
              <button
                className="preview-choice"
                type="button"
                aria-pressed={!showResource}
                onClick={() => setShowResource(false)}
              >
                Sin recurso
              </button>
              <button
                className="preview-choice"
                type="button"
                aria-pressed={showResource}
                onClick={() => setShowResource(true)}
              >
                Visual
              </button>
            </div>
          </fieldset>
        </div>
      </section>

      <div className="test-stage">
        <TestScreen
          testName="Subprueba de muestra"
          questionNumber={questionNumber}
          totalQuestions={3}
          timeRemainingSeconds={phase === 'active' ? 272 : null}
          prompt="Palabra de ejemplo"
          options={demoOptions}
          responseType={responseType}
          selectedOption={selectedOption}
          textResponse={textResponse}
          saveState={saveState}
          notice={getPreviewNotice(noticeKind)}
          phase={phase}
          questionResource={getPreviewResource(showResource)}
          primaryActionLabel={getPreviewActionLabel(phase)}
          onSelect={setSelectedOption}
          onTextResponse={setTextResponse}
          onNext={handleNext}
        />
      </div>
    </main>
  );
}
