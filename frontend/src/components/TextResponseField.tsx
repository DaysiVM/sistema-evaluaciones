type TextResponseFieldProps = {
  value: string;
  label?: string;
  maxLength?: number;
  onChange: (value: string) => void;
};

export function TextResponseField({
  value,
  label = 'Respuesta',
  maxLength,
  onChange,
}: TextResponseFieldProps) {
  return (
    <label className="text-response-field">
      <span className="text-response-label">{label}</span>
      <textarea
        className="text-response"
        value={value}
        maxLength={maxLength}
        onChange={(event) => onChange(event.target.value)}
      />
    </label>
  );
}
