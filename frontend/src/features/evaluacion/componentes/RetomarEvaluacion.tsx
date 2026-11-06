import { useId, useState, type FormEvent } from 'react';
import { useRetomarEvaluacion } from '../hooks/useRetomarEvaluacion';
import type { EvaluacionResponse, PeriodoResponse, TestDetalleResponse } from '../tipos';
import { validarId } from '../validaciones';
import { ErrorCampo } from './ErrorCampo';
import { EstadoCarga } from './EstadoCarga';
import { MensajeError } from './MensajeError';
import { ResumenEvaluacion } from './ResumenEvaluacion';

interface RetomarEvaluacionProps {
  test: TestDetalleResponse | null;
  periodos: PeriodoResponse[];
  onContinuar: (evaluacion: EvaluacionResponse) => void;
}

/** E4: recupera una evaluación por ID y muestra el estado que devuelve el servidor. */
export function RetomarEvaluacion({ test, periodos, onContinuar }: RetomarEvaluacionProps) {
  const id = useId();
  const [texto, setTexto] = useState('');
  const [error, setError] = useState<string | null>(null);
  const { estado, ejecutar, reiniciar } = useRetomarEvaluacion();

  const recuperar = () => ejecutar(Number(texto.trim()));

  async function enviar(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault();
    const errorId = validarId(texto, 'de la evaluación');
    setError(errorId);
    if (errorId) return;
    await recuperar();
  }

  function cambiar(valor: string) {
    setTexto(valor);
    setError(null);
    // El resultado mostrado corresponde al ID anterior; se limpia para no confundir.
    if (estado.fase !== 'inicial') reiniciar();
  }

  return (
    <div className="ev-retomar">
      <form className="ev-form" onSubmit={(evento) => void enviar(evento)} noValidate>
        <fieldset className="ev-fieldset" disabled={estado.fase === 'cargando'}>
          <legend className="visually-hidden">Retomar una evaluación existente</legend>
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
                Recuperar
              </button>
            </div>
            <ErrorCampo id={`${id}-evaluacion-error`} mensaje={error ?? undefined} />
            <p className="ev-ayuda">Recuperar no crea otro intento ni reinicia el reloj.</p>
          </div>
        </fieldset>
      </form>

      {estado.fase === 'cargando' && <EstadoCarga texto="Recuperando la evaluación…" />}
      {estado.fase === 'error' && (
        <MensajeError error={estado.error} onReintentar={() => void recuperar()} />
      )}
      {estado.fase === 'exito' && (
        <ResumenEvaluacion
          evaluacion={estado.datos}
          test={test}
          periodos={periodos}
          titulo="Evaluación recuperada"
          onContinuar={onContinuar}
        />
      )}
    </div>
  );
}
