import { useId, useState, type FormEvent, type KeyboardEvent, type ReactNode } from 'react';
import { useBuscarSujeto } from '../hooks/useBuscarSujeto';
import { useIniciarEvaluacion } from '../hooks/useIniciarEvaluacion';
import { formatearSegundos } from '../progreso';
import type { EvaluacionResponse, PeriodoResponse, TestDetalleResponse } from '../tipos';
import {
  incluyeFluidez,
  validarId,
  validarInicio,
  VALORES_INICIO_VACIOS,
  type CampoInicio,
  type ErroresInicio,
  type ValoresInicio,
} from '../validaciones';
import { ConfiguracionesTest } from './ConfiguracionesTest';
import { ErrorCampo } from './ErrorCampo';
import { EstadoCarga } from './EstadoCarga';
import { MensajeError } from './MensajeError';

interface FormularioInicioProps {
  test: TestDetalleResponse;
  periodos: PeriodoResponse[];
  /** Se llama solo con la evaluación que el servidor confirmó (E3 201). */
  onIniciada: (evaluacion: EvaluacionResponse) => void;
}

function Paso({
  numero,
  titulo,
  children,
}: {
  numero: number;
  titulo: string;
  children: ReactNode;
}) {
  const id = useId();
  return (
    <section className="ev-paso" aria-labelledby={id}>
      <h2 id={id} className="ev-paso-titulo">
        <span className="ev-paso-numero" aria-hidden="true">
          {numero}
        </span>
        {titulo}
      </h2>
      <div className="ev-paso-contenido">{children}</div>
    </section>
  );
}

function DatoResumen({ etiqueta, valor }: { etiqueta: string; valor: string | null }) {
  return (
    <div>
      <dt>{etiqueta}</dt>
      <dd className={valor ? undefined : 'ev-tenue'}>{valor ?? 'Pendiente'}</dd>
    </div>
  );
}

export function FormularioInicio({ test, periodos, onIniciada }: FormularioInicioProps) {
  const id = useId();
  const [valores, setValores] = useState<ValoresInicio>(VALORES_INICIO_VACIOS);
  const [errores, setErrores] = useState<ErroresInicio>({});
  const busqueda = useBuscarSujeto();
  const inicio = useIniciarEvaluacion();

  const sujeto = busqueda.estado.fase === 'exito' ? busqueda.estado.datos : null;
  const enviando = inicio.estado.fase === 'cargando';
  const conFluidez = incluyeFluidez(valores, test);

  // Datos del resumen (solo lectura de lo elegido; no crea nada).
  const periodo = periodos.find((p) => String(p.id) === valores.periodoId);
  const incluidas =
    valores.aplicacion === 'completa'
      ? test.subpruebas
      : test.subpruebas.filter((s) => valores.subpruebas.includes(s.codigo));
  const segundos = incluidas.reduce((suma, s) => suma + s.tiempoLimiteSegundos, 0);
  const letra = valores.letraInicial.trim().toUpperCase();

  const idError = (campo: CampoInicio) => `${id}-${campo}-error`;
  const accesibilidad = (campo: CampoInicio) =>
    errores[campo] ? { 'aria-invalid': true, 'aria-describedby': idError(campo) } : {};

  function cambiar<K extends CampoInicio>(campo: K, valor: ValoresInicio[K]) {
    setValores((previos) => ({ ...previos, [campo]: valor }));
    setErrores((previos) => ({ ...previos, [campo]: undefined }));
    // Un evaluado confirmado deja de valer si cambia el ID.
    if (campo === 'sujetoId') busqueda.reiniciar();
    if (inicio.estado.fase === 'error') inicio.reiniciar();
  }

  async function buscarSujeto() {
    const error = validarId(valores.sujetoId, 'del evaluado');
    if (error) {
      setErrores((previos) => ({ ...previos, sujetoId: error }));
      return;
    }
    await busqueda.ejecutar(Number(valores.sujetoId.trim()));
  }

  function alSoltarTeclaSujeto(evento: KeyboardEvent<HTMLInputElement>) {
    if (evento.key === 'Enter') {
      evento.preventDefault();
      void buscarSujeto();
    }
  }

  function alternarSubprueba(codigo: string, marcada: boolean) {
    cambiar(
      'subpruebas',
      marcada
        ? [...valores.subpruebas, codigo]
        : valores.subpruebas.filter((actual) => actual !== codigo),
    );
  }

  async function enviar(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault();
    if (enviando) return;
    const resultado = validarInicio(valores, test, sujeto?.id ?? null);
    if (!resultado.valido) {
      setErrores(resultado.errores);
      return;
    }
    setErrores({});
    const creada = await inicio.ejecutar(resultado.solicitud);
    // No se avanza ni se guarda nada si el servidor no confirmó la creación.
    if (creada) onIniciada(creada);
  }

  return (
    <form className="ev-form" onSubmit={(evento) => void enviar(evento)} noValidate>
      <fieldset className="ev-fieldset" disabled={enviando}>
        <legend className="visually-hidden">Iniciar una evaluación nueva</legend>

        <Paso numero={1} titulo="Evaluado">
          <div className="ev-campo">
            <label htmlFor={`${id}-sujeto`}>ID del evaluado</label>
            <div className="ev-fila">
              <input
                id={`${id}-sujeto`}
                className="ev-input"
                inputMode="numeric"
                autoComplete="off"
                value={valores.sujetoId}
                onChange={(evento) => cambiar('sujetoId', evento.target.value)}
                onKeyDown={alSoltarTeclaSujeto}
                {...accesibilidad('sujetoId')}
              />
              <button
                type="button"
                className="ev-boton ev-boton--secundario"
                onClick={() => void buscarSujeto()}
                disabled={busqueda.estado.fase === 'cargando'}
              >
                Buscar
              </button>
            </div>
            <ErrorCampo id={idError('sujetoId')} mensaje={errores.sujetoId} />
            {busqueda.estado.fase === 'cargando' && <EstadoCarga texto="Buscando evaluado…" />}
            {busqueda.estado.fase === 'error' && (
              <MensajeError
                error={busqueda.estado.error}
                onReintentar={() => void buscarSujeto()}
              />
            )}
            {sujeto && (
              <p className="ev-confirmado" role="status">
                <span aria-hidden="true">✓</span> {sujeto.nombres} {sujeto.apellidos} ·{' '}
                {sujeto.identificadorInstitucional}
              </p>
            )}
          </div>
        </Paso>

        <Paso numero={2} titulo="Período y tipo de aplicación">
          <div className="ev-campo">
            <label htmlFor={`${id}-periodo`}>Período</label>
            {periodos.length === 0 ? (
              <p className="ev-ayuda">
                No hay períodos disponibles para nuevas evaluaciones. Un Coordinador debe crear uno.
              </p>
            ) : (
              <select
                id={`${id}-periodo`}
                className="ev-input"
                value={valores.periodoId}
                onChange={(evento) => cambiar('periodoId', evento.target.value)}
                {...accesibilidad('periodoId')}
              >
                <option value="">Seleccione…</option>
                {periodos.map((p) => (
                  <option key={p.id} value={String(p.id)}>
                    {p.codigo} — {p.denominacion}
                  </option>
                ))}
              </select>
            )}
            <ErrorCampo id={idError('periodoId')} mensaje={errores.periodoId} />
          </div>

          <fieldset className="ev-opciones">
            <legend>Tipo de aplicación</legend>
            <label>
              <input
                type="radio"
                name={`${id}-aplicacion`}
                checked={valores.aplicacion === 'completa'}
                onChange={() => cambiar('aplicacion', 'completa')}
              />{' '}
              Completa ({test.subpruebas.length} subpruebas)
            </label>
            <label>
              <input
                type="radio"
                name={`${id}-aplicacion`}
                checked={valores.aplicacion === 'parcial'}
                onChange={() => cambiar('aplicacion', 'parcial')}
              />{' '}
              Parcial (elegir subpruebas)
            </label>
          </fieldset>
        </Paso>

        <Paso numero={3} titulo="Subpruebas">
          <ConfiguracionesTest
            test={test}
            seleccion={
              valores.aplicacion === 'parcial'
                ? { seleccionadas: valores.subpruebas, onCambiar: alternarSubprueba }
                : undefined
            }
            error={errores.subpruebas}
            errorId={idError('subpruebas')}
          />

          {conFluidez && (
            <div className="ev-campo">
              <label htmlFor={`${id}-letra`}>Letra inicial de Fluidez Verbal</label>
              <input
                id={`${id}-letra`}
                className="ev-input ev-input--corto"
                maxLength={1}
                autoComplete="off"
                value={valores.letraInicial}
                onChange={(evento) => cambiar('letraInicial', evento.target.value)}
                {...accesibilidad('letraInicial')}
              />
              <p className="ev-ayuda">Obligatoria porque la aplicación incluye FV (RF-FV-06).</p>
              <ErrorCampo id={idError('letraInicial')} mensaje={errores.letraInicial} />
            </div>
          )}
        </Paso>

        <div className="ev-confirmar">
          <dl className="ev-resumen-inicio" aria-label="Resumen de la evaluación a crear">
            <DatoResumen
              etiqueta="Evaluado"
              valor={sujeto ? `${sujeto.nombres} ${sujeto.apellidos}` : null}
            />
            <DatoResumen etiqueta="Período" valor={periodo?.codigo ?? null} />
            <DatoResumen
              etiqueta="Subpruebas"
              valor={`${incluidas.length} de ${test.subpruebas.length}`}
            />
            <DatoResumen
              etiqueta="Tiempo oficial"
              valor={incluidas.length > 0 ? `${formatearSegundos(segundos)} min` : null}
            />
            {conFluidez && <DatoResumen etiqueta="Letra FV" valor={letra || null} />}
          </dl>
          <button type="submit" className="ev-boton">
            {enviando ? 'Iniciando…' : 'Iniciar evaluación'}
          </button>
        </div>
      </fieldset>

      {enviando && <EstadoCarga texto="Creando la evaluación en el servidor…" />}
      {inicio.estado.fase === 'error' && <MensajeError error={inicio.estado.error} />}
    </form>
  );
}
