export type TestOption = {
  id: string;
  label: string;
};

type AnswerOptionProps = {
  groupName: string;
  option: TestOption;
  checked: boolean;
  onChange: (optionId: string) => void;
};

export function AnswerOption({ groupName, option, checked, onChange }: AnswerOptionProps) {
  const optionId = `${groupName}-${option.id}`;

  return (
    <label className={`answer-option${checked ? ' is-selected' : ''}`} htmlFor={optionId}>
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
      <span className="answer-option-label">{option.label}</span>
    </label>
  );
}
