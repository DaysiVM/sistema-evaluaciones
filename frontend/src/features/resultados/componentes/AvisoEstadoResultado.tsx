import { describirEstadoResultado } from '../presentacion';
import type { ResultadoResponse } from '../tipos';
import { Insignia } from './Insignia';

/** Estado del contenedor de resultados (CALCULANDO, PENDIENTE_VALIDACION o DISPONIBLE). */
export function AvisoEstadoResultado({ resultado }: { resultado: ResultadoResponse }) {
  const { etiqueta, tono, detalle } = describirEstadoResultado(resultado.estadoResultado);
  return (
    <div className={`res-aviso res-aviso--${tono}`} role="status">
      <Insignia texto={etiqueta} tono={tono} />
      <p>{detalle}</p>
    </div>
  );
}
