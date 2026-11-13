import { TestNotice } from '../../../components/TestNotice';
import { etiquetaCampo, TITULOS_ERROR } from '../mensajesError';
import type { ErrorMostrable } from '../tipos';

interface MensajeErrorProps {
  error: ErrorMostrable;
  /** Solo se ofrece en errores de conexión o del servidor. */
  onReintentar?: () => void;
}

/** Aviso de error común: reutiliza TestNotice y agrega los detalles por campo. */
export function MensajeError({ error, onReintentar }: MensajeErrorProps) {
  const reintentable = onReintentar && (error.tipo === 'conexion' || error.tipo === 'servidor');

  return (
    <div className={`ev-error ev-error--${error.tipo}`}>
      <TestNotice kind="error" message={`${TITULOS_ERROR[error.tipo]}. ${error.mensaje}`} />
      {error.detalles.length > 0 && (
        <ul className="ev-error-detalles">
          {error.detalles.map((detalle) => (
            <li key={`${detalle.campo}-${detalle.mensaje}`}>
              <strong>{etiquetaCampo(detalle.campo)}</strong>: {detalle.mensaje}
            </li>
          ))}
        </ul>
      )}
      {reintentable && (
        <button type="button" className="ev-boton ev-boton--secundario" onClick={onReintentar}>
          Reintentar
        </button>
      )}
    </div>
  );
}
