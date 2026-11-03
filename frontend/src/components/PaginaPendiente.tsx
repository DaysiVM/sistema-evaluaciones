/** Marcador de una página que aún no implementa su tarea. */
export function PaginaPendiente({ nombre }: { nombre: string }) {
  return (
    <>
      <h1>Sistema Integrado de Evaluaciones</h1>
      <p>{nombre}: pendiente de implementación.</p>
    </>
  );
}
