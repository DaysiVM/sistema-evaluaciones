import { CODIGOS_NUMERICO, CONFIG_NUMERICO, type CodigoNumerico } from '../subpruebas';
import '../numerico.css';

export type EstadoEnFamilia = 'pendiente' | 'en-curso' | 'cerrada';

const TEXTO_ESTADO: Record<EstadoEnFamilia, string> = {
  pendiente: 'Pendiente',
  'en-curso': 'En curso',
  cerrada: 'Cerrada',
};

/**
 * Avance de la familia Numérico en el orden oficial. Es solo informativo: no permite saltar
 * ni volver a una subprueba (Documento BFA §2.6).
 */
export function AvanceFamilia({ estados }: { estados: Record<CodigoNumerico, EstadoEnFamilia> }) {
  return (
    <ol className="num-avance" aria-label="Avance de la sección numérica">
      {CODIGOS_NUMERICO.map((codigo) => {
        const estado = estados[codigo];
        return (
          <li
            key={codigo}
            className={`num-avance-paso num-avance-paso--${estado}`}
            aria-current={estado === 'en-curso' ? 'step' : undefined}
          >
            <span className="num-avance-codigo">{codigo}</span>
            <span className="num-avance-nombre">
              {CONFIG_NUMERICO[codigo].nombre.replace('Numérico · ', '')}
            </span>
            <span className="num-avance-estado">{TEXTO_ESTADO[estado]}</span>
          </li>
        );
      })}
    </ol>
  );
}
