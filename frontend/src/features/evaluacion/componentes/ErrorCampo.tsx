/** Mensaje de validación bajo un campo; su id se enlaza con aria-describedby. */
export function ErrorCampo({ id, mensaje }: { id: string; mensaje?: string }) {
  if (!mensaje) return null;
  return (
    <p id={id} className="ev-campo-error">
      {mensaje}
    </p>
  );
}
