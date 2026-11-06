import type { ReactNode } from 'react';

export type TestOption = {
  id: string;
  /** Texto de la alternativa. Si hay `figure`, queda como nombre accesible y no se ve. */
  label: ReactNode;
  /** Figura de la alternativa (en la API llega como `recursoUrl`). */
  figure?: ReactNode;
};

/** `binary`: dos alternativas grandes lado a lado (Verdadero / Falso). */
export type OptionVariant = 'row' | 'binary';

type AnswerOptionProps = {
  groupName: string;
  option: TestOption;
  checked: boolean;
  variant?: OptionVariant;
  onChange: (optionId: string) => void;
};

export function AnswerOption({
  groupName,
  option,
  checked,
  variant = 'row',
  onChange,
}: AnswerOptionProps) {
  const optionId = `${groupName}-${option.id}`;
  const className = [
    'answer-option',
    variant === 'binary' && 'answer-option--binary',
    option.figure && 'answer-option--figure',
    checked && 'is-selected',
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <label className={className} htmlFor={optionId}>
      <input
        className="visually-hidden"
        id={optionId}
        name={groupName}
        type="radio"
        value={option.id}
        checked={checked}
        onChange={() => onChange(option.id)}
      />
      <span className="option-marker" aria-hidden="true">
        {option.id.toUpperCase()}
      </span>
      {option.figure && <span aria-hidden="true">{option.figure}</span>}
      <span className={option.figure ? 'visually-hidden' : 'answer-option-label'}>
        {option.label}
      </span>
    </label>
  );
}
