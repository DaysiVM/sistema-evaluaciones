import { useId, type ReactNode } from 'react';
import { AnswerOption, type TestOption } from './AnswerOption';
import { SaveStatus, type SaveState } from './SaveStatus';
import { TestNotice, type TestNoticeData } from './TestNotice';
import { TextResponseField } from './TextResponseField';

export type TestPhase = 'instructions' | 'practice' | 'active' | 'closed';
export type TestResponseType = 'selection' | 'text';

type TestScreenProps = {
  testName: string;
  questionNumber: number;
  totalQuestions: number;
  timeRemainingSeconds: number | null;
  prompt: ReactNode;
  options?: TestOption[];
  responseType?: TestResponseType;
  selectedOption: string | null;
  textResponse?: string;
  saveState: SaveState;
  notice?: TestNoticeData;
  phase?: TestPhase;
  phaseTitle?: string;
  phaseDescription?: string;
  questionResource?: ReactNode;
  primaryActionLabel?: string;
  onSelect: (optionId: string) => void;
  onTextResponse?: (value: string) => void;
  onNext: () => void;
};

function formatRemainingTime(seconds: number | null) {
  if (seconds === null) return '—';

  const safeSeconds = Math.max(0, seconds);
  const minutes = Math.floor(safeSeconds / 60);
  const remainingSeconds = safeSeconds % 60;

  return `${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
}

const phaseDefaults: Record<
  Exclude<TestPhase, 'active'>,
  { label: string; title: string; description: string }
> = {
  instructions: {
    label: 'Instrucciones',
    title: 'Instrucciones de ejemplo',
    description: 'Esta pantalla representa las instrucciones previas a la subprueba.',
  },
  practice: {
    label: 'Práctica',
    title: 'Práctica de ejemplo',
    description: 'Esta pantalla representa una práctica que no consume tiempo oficial.',
  },
  closed: {
    label: 'Cerrada',
    title: 'Subprueba cerrada',
    description: 'Esta pantalla representa una subprueba que ya no recibe respuestas.',
  },
};

export function TestScreen({
  testName,
  questionNumber,
  totalQuestions,
  timeRemainingSeconds,
  prompt,
  options = [],
  responseType = 'selection',
  selectedOption,
  textResponse = '',
  saveState,
  notice,
  phase = 'active',
  phaseTitle,
  phaseDescription,
  questionResource,
  primaryActionLabel,
  onSelect,
  onTextResponse,
  onNext,
}: TestScreenProps) {
  const headingId = useId();
  const isActiveQuestion = phase === 'active';
  const phaseCopy = phase === 'active' ? null : phaseDefaults[phase];
  const timerValue = formatRemainingTime(timeRemainingSeconds);
  const timerSeconds =
    timeRemainingSeconds === null ? undefined : Math.max(0, timeRemainingSeconds);
  const sectionHeadingId = isActiveQuestion ? headingId : `${headingId}-phase`;

  return (
    <section className="test-paper" aria-labelledby={sectionHeadingId}>
      <header className="test-header">
        <span className="test-name">{testName}</span>
        <time
          className="test-timer"
          dateTime={timerSeconds === undefined ? undefined : `PT${timerSeconds}S`}
        >
          {timerValue}
        </time>
      </header>

      <div className="test-content">
        {isActiveQuestion ? (
          <>
            <p className="question-counter">
              Pregunta {questionNumber} de {totalQuestions}
            </p>
            <h1 className="question-prompt" id={headingId}>
              {prompt}
            </h1>

            {questionResource && <div className="question-resource">{questionResource}</div>}
            {notice && <TestNotice {...notice} />}

            {responseType === 'selection' ? (
              <div className="options" role="radiogroup" aria-label="Alternativas de respuesta">
                {options.map((option) => (
                  <AnswerOption
                    key={option.id}
                    groupName={`test-question-${headingId}`}
                    option={option}
                    checked={selectedOption === option.id}
                    onChange={onSelect}
                  />
                ))}
              </div>
            ) : (
              <TextResponseField
                value={textResponse}
                onChange={onTextResponse ?? (() => undefined)}
              />
            )}
          </>
        ) : (
          <section className="phase-panel" aria-labelledby={`${headingId}-phase`}>
            <p className="phase-label">{phaseCopy?.label}</p>
            <h1 className="question-prompt" id={`${headingId}-phase`}>
              {phaseTitle ?? phaseCopy?.title}
            </h1>
            <p className="phase-description">{phaseDescription ?? phaseCopy?.description}</p>
            {notice && <TestNotice {...notice} />}
          </section>
        )}
      </div>

      <footer className="test-footer">
        <SaveStatus state={saveState} />
        <button className="primary-action" type="button" onClick={onNext}>
          {primaryActionLabel ?? (phase === 'closed' ? 'Continuar' : 'Siguiente')}
        </button>
      </footer>
    </section>
  );
}
