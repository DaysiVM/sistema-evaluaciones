/** Indicador de carga accesible (se anuncia a lectores de pantalla). */
export function EstadoCarga({ texto }: { texto: string }) {
  return (
    <p className="ev-cargando" role="status" aria-live="polite">
      <span className="ev-spinner" aria-hidden="true" />
      {texto}
    </p>
  );
}
