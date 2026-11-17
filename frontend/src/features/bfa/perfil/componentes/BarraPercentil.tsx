/**
 * Marca del percentil en la escala 1–99 del baremo. Solo se dibuja con un percentil
 * recibido dentro de esa escala; no hay bandas ni etiquetas de interpretación.
 */
export function BarraPercentil({ percentil }: { percentil: number | null }) {
  if (percentil === null || percentil < 1 || percentil > 99) {
    return (
      <span className="perfil-barra perfil-barra--vacia">
        <span className="visually-hidden">Sin percentil</span>
      </span>
    );
  }
  // Posición en la pista (1 = inicio, 99 = final): es solo ubicación visual del valor recibido.
  const posicion = ((percentil - 1) / 98) * 100;
  return (
    <span
      className="perfil-barra"
      role="img"
      aria-label={`Percentil ${percentil} en una escala de 1 a 99`}
    >
      <span className="perfil-barra-relleno" style={{ width: `${posicion}%` }} />
    </span>
  );
}
