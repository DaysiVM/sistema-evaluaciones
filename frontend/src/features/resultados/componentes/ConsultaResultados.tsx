import { useEffect, useId, useState, type FormEvent } from 'react';
import { EstadoCarga } from '../../evaluacion/componentes/EstadoCarga';
import { ErrorCampo } from '../../evaluacion/componentes/ErrorCampo';
import { MensajeError } from '../../evaluacion/componentes/MensajeError';
import { TestNotice } from '../../../components/TestNotice';
import { validarId } from '../../evaluacion/validaciones';
import { usaMocksResultados } from '../cliente';
import { esNoFinalizada } from '../errores';
import { useResultado } from '../hooks/useResultado';
import type { ClienteResultados } from '../tipos';
import type { TablaFactoresProps } from './TablaFactores';
import { VistaResultado } from './VistaResultado';

interface ConsultaResultadosProps extends Pick<TablaFactoresProps, 'etiquetaFactor'> {
  /** Si viene (p. ej. desde la ruta), se consulta al montar. */
  evaluacionIdInicial?: string;
  cliente?: ClienteResultados;
}

/** Buscador por ID + estados de carga, error y resultado de E9. */
export function ConsultaResultados({
  evaluacionIdInicial,
  cliente,
  etiquetaFactor,
}: ConsultaResultadosProps) {
  const id = useId();
  const [texto, setTexto] = useState(evaluacionIdInicial ?? '');
  const [error, setError] = useState<string | null>(null);
  const [consultado, setConsultado] = useState<number | null>(null);
  const { estado, ejecutar, reiniciar } = useResultado(cliente);

  const consultar = (valor: string) => {
    const errorId = validarId(valor, 'de la evaluación');
    setError(errorId);
    if (errorId) return;
    const evaluacionId = Number(valor.trim());
    setConsultado(evaluacionId);
    void ejecutar(evaluacionId);
  };

  useEffect(() => {
    if (evaluacionIdInicial) consultar(evaluacionIdInicial);
    // Solo al montar o si cambia el ID de la ruta.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [evaluacionIdInicial]);

  function enviar(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault();
    consultar(texto);
  }

  function cambiar(valor: string) {
    setTexto(valor);
    setError(null);
    // El resultado mostrado pertenece al ID anterior; se limpia para no confundir.
    if (estado.fase !== 'inicial') reiniciar();
  }

  return (
    <div className="res-consulta">
      <form className="ev-form" onSubmit={enviar} noValidate>
        <fieldset className="ev-fieldset" disabled={estado.fase === 'cargando'}>
          <legend className="visually-hidden">Consultar resultados de una evaluación</legend>
          <div className="ev-campo">
            <label htmlFor={`${id}-evaluacion`}>ID de la evaluación</label>
            <div className="ev-fila">
              <input
                id={`${id}-evaluacion`}
                className="ev-input"
                inputMode="numeric"
                autoComplete="off"
                value={texto}
                onChange={(evento) => cambiar(evento.target.value)}
                {...(error
                  ? { 'aria-invalid': true, 'aria-describedby': `${id}-evaluacion-error` }
                  : {})}
              />
              <button type="submit" className="ev-boton">
                Consultar
              </button>
            </div>
            <ErrorCampo id={`${id}-evaluacion-error`} mensaje={error ?? undefined} />
            <p className="ev-ayuda">Solo usuarios autorizados y evaluaciones finalizadas.</p>
          </div>
        </fieldset>
      </form>

      {estado.fase === 'cargando' && <EstadoCarga texto="Consultando resultados…" />}
      {estado.fase === 'error' &&
        (esNoFinalizada(estado.error) ? (
          <TestNotice kind="warning" message={estado.error.mensaje} />
        ) : (
          <MensajeError
            error={estado.error}
            onReintentar={consultado !== null ? () => void ejecutar(consultado) : undefined}
          />
        ))}
      {estado.fase === 'exito' && (
        <VistaResultado
          resultado={estado.datos}
          datosSimulados={usaMocksResultados && !cliente}
          etiquetaFactor={etiquetaFactor}
        />
      )}
    </div>
  );
}
