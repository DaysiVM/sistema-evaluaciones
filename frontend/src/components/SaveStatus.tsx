export type SaveState = 'saved' | 'saving' | 'error';

type SaveStatusProps = {
  state: SaveState;
};

const saveStateLabels: Record<SaveState, string> = {
  saved: 'Guardado',
  saving: 'Guardando',
  error: 'No se pudo guardar',
};

const saveStateIcons: Record<SaveState, string> = {
  saved: '✓',
  saving: '···',
  error: '!',
};

export function SaveStatus({ state }: SaveStatusProps) {
  return (
    <p className={`save-status save-status--${state}`} role="status" aria-live="polite">
      <span className="status-icon" aria-hidden="true">
        {saveStateIcons[state]}
      </span>
      {saveStateLabels[state]}
    </p>
  );
}
