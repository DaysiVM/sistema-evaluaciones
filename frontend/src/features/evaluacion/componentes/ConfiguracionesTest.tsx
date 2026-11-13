import { formatearSegundos } from '../progreso';
import type { ConfiguracionSubprueba, TestDetalleResponse } from '../tipos';

interface SeleccionSubpruebas {
  seleccionadas: string[];
  onCambiar: (codigo: string, marcada: boolean) => void;
}

interface ConfiguracionesTestProps {
  test: TestDetalleResponse;
  /** Si se pasa, cada fila tiene una casilla (aplicación parcial). */
  seleccion?: SeleccionSubpruebas;
  error?: string;
  errorId?: string;
}

function describirRespuesta(s: ConfiguracionSubprueba): string {
  if (s.tipoRespuesta === 'TEXTO_LIBRE') {
    return s.maximoEntradas ? `Texto libre (hasta ${s.maximoEntradas})` : 'Texto libre';
  }
  if (!s.opciones) return 'Selección';
  return `Selección A–${String.fromCharCode(64 + s.opciones)}`;
}

/** Configuraciones permitidas de la edición publicada (E2), en orden de aplicación. */
export function ConfiguracionesTest({ test, seleccion, error, errorId }: ConfiguracionesTestProps) {
  const subpruebas = [...test.subpruebas].sort((a, b) => a.orden - b.orden);

  return (
    <div className="ev-configuraciones">
      <div className="ev-tabla-contenedor">
        <table
          className={`ev-tabla${seleccion ? ' ev-tabla--seleccion' : ''}`}
          aria-describedby={error ? errorId : undefined}
        >
          <caption>
            Configuraciones permitidas · {test.nombre} ({test.codigo})
          </caption>
          <thead>
            <tr>
              {seleccion && (
                <th scope="col" className="ev-col-check">
                  <span className="visually-hidden">Incluir</span>
                </th>
              )}
              <th scope="col" className="ev-col-orden">
                Orden
              </th>
              <th scope="col">Subprueba</th>
              <th scope="col">Tiempo</th>
              <th scope="col">Respuesta</th>
              <th scope="col">Preguntas</th>
            </tr>
          </thead>
          <tbody>
            {subpruebas.map((s) => {
              const marcada = seleccion?.seleccionadas.includes(s.codigo) ?? true;
              const casillaId = `ev-sub-${s.codigo}`;
              const clase = seleccion ? (marcada ? 'ev-fila-incluida' : 'ev-fila-excluida') : '';
              const definidas = s.tipoRespuesta === 'SELECCION' && s.numeroPreguntas > 0;
              return (
                <tr key={s.codigo} className={clase || undefined}>
                  {seleccion && (
                    <td className="ev-col-check">
                      <input
                        id={casillaId}
                        type="checkbox"
                        checked={marcada}
                        onChange={(e) => seleccion.onCambiar(s.codigo, e.target.checked)}
                      />
                    </td>
                  )}
                  <td className="ev-col-orden">{s.orden}</td>
                  <td className="ev-col-nombre">
                    {seleccion ? <label htmlFor={casillaId}>{s.nombre}</label> : s.nombre}{' '}
                    <span className="ev-codigo">{s.codigo}</span>
                  </td>
                  <td className="ev-col-tiempo ev-numero">
                    {formatearSegundos(s.tiempoLimiteSegundos)}
                  </td>
                  <td className="ev-col-respuesta">{describirRespuesta(s)}</td>
                  <td
                    className={`ev-col-preguntas ev-numero${definidas ? '' : ' ev-tenue'}`}
                    data-etiqueta="Preguntas"
                  >
                    {s.tipoRespuesta === 'TEXTO_LIBRE'
                      ? '—'
                      : definidas
                        ? s.numeroPreguntas
                        : 'Por definir'}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
      <p className="ev-ayuda">Instrucciones y práctica no consumen tiempo oficial.</p>
      {error && (
        <p id={errorId} className="ev-campo-error">
          {error}
        </p>
      )}
    </div>
  );
}
