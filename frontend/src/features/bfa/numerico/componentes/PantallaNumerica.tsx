import { useEffect, useId } from 'react';
import type { TestOption } from '../../../../components/AnswerOption';
import { SaveStatus, type SaveState } from '../../../../components/SaveStatus';
import { TestNotice, type TestNoticeData } from '../../../../components/TestNotice';
import { TestScreen } from '../../../../components/TestScreen';
import type { PreguntaSubprueba } from '../../../../services/subpruebas';
import type { FormatoOperacion } from '../notacion';
import { ExpresionMatematica, TextoMatematico } from './Notacion';
import {
  CONFIG_NUMERICO,
  formatearTiempo,
  letraDeTecla,
  type CodigoNumerico,
  type ConfigNumerica,
} from '../subpruebas';
import '../numerico.css';

/**
 * `espera`: la persona ya respondió, pero la subprueba sigue activa hasta que el backend la cierre.
 * El manual no permite pasar a la siguiente prueba antes del límite (Documento BFA §2.6).
 */
export type FaseNumerica = 'instrucciones' | 'activa' | 'espera' | 'cerrada';

export type PantallaNumericaProps = {
  codigo: CodigoNumerico;
  fase: FaseNumerica;
  /** Pregunta actual tal como llega en E6; no se usa en instrucciones ni en cierre. */
  pregunta?: PreguntaSubprueba;
  /** Posición (desde 0) de la pregunta actual y total recibido en E6. */
  indice: number;
  total: number;
  /** Letra marcada en la pregunta actual. */
  seleccion: string | null;
  estadoGuardado: SaveState;
  /** Tiempo restante informado por el backend; `null` antes de E5. */
  segundosRestantes: number | null;
  aviso?: TestNoticeData;
  /** Disposición de Sumas y Multiplicaciones (material pendiente: vertical u horizontal). */
  formato?: FormatoOperacion;
  /** `true` si se cerró al agotarse el tiempo. */
  cerradaPorTiempo?: boolean;
  /** Respuestas guardadas, para el texto de la espera. */
  respondidas?: number;
  onIniciar: () => void;
  onSeleccionar: (letra: string) => void;
  onSiguiente?: () => void;
  onAnterior?: () => void;
  /** Última pregunta: la persona indica que terminó y pasa a la espera. */
  onTerminar?: () => void;
  /** Desde la espera, vuelve a las preguntas (solo si la subprueba lo permite). */
  onRevisar?: () => void;
  /** Lo decide el flujo BFA: esta pantalla no navega a otras subpruebas. */
  onContinuar?: () => void;
};

/** Alternativas tal como llegan en E6, en su orden y con su letra; sin marcar ninguna como correcta. */
function opciones(pregunta: PreguntaSubprueba | undefined, binaria: boolean): TestOption[] {
  return (pregunta?.opciones ?? []).map((o) => ({
    id: o.letra,
    label: binaria ? o.texto : <TextoMatematico texto={o.texto} />,
  }));
}

/**
 * Pantalla de una subprueba numérica sobre `TestScreen` (UI-01/UI-04). Es solo presentación:
 * recibe pregunta, selección, guardado y tiempo, y avisa de lo que hace la persona.
 */
export function PantallaNumerica({
  codigo,
  fase,
  pregunta,
  indice,
  total,
  seleccion,
  estadoGuardado,
  segundosRestantes,
  aviso,
  formato = 'horizontal',
  cerradaPorTiempo = false,
  respondidas = 0,
  onIniciar,
  onSeleccionar,
  onSiguiente,
  onAnterior,
  onTerminar,
  onRevisar,
  onContinuar,
}: PantallaNumericaProps) {
  const config = CONFIG_NUMERICO[codigo];
  const binaria = config.respuesta === 'binaria';
  const activa = fase === 'activa' && pregunta !== undefined;

  // Respuesta por teclado: evita mover el ratón en las subpruebas de 4 minutos.
  useEffect(() => {
    if (!activa) return;
    const alPulsar = (evento: KeyboardEvent) => {
      if (evento.altKey || evento.ctrlKey || evento.metaKey || evento.repeat) return;
      const destino = evento.target as HTMLElement | null;
      if (destino?.closest('input[type="text"], textarea, [contenteditable="true"]')) return;
      const letra = letraDeTecla(config, evento.key);
      if (letra && pregunta?.opciones.some((o) => o.letra === letra)) {
        evento.preventDefault();
        onSeleccionar(letra);
      }
    };
    window.addEventListener('keydown', alPulsar);
    return () => window.removeEventListener('keydown', alPulsar);
  }, [activa, config, pregunta, onSeleccionar]);

  const prompt = !pregunta ? (
    ''
  ) : config.contenido === 'problema' ? (
    <TextoMatematico texto={pregunta.enunciado} />
  ) : (
    <ExpresionMatematica
      expresion={pregunta.enunciado}
      formato={config.contenido === 'operacion' ? formato : 'horizontal'}
    />
  );

  const esUltima = total > 0 && indice >= total - 1;
  const etiquetaPrincipal =
    fase === 'instrucciones'
      ? 'Comenzar'
      : fase === 'cerrada'
        ? 'Continuar'
        : esUltima
          ? 'Terminé'
          : 'Siguiente';

  const alPrincipal = () => {
    if (fase === 'instrucciones') return onIniciar();
    if (fase === 'cerrada') return onContinuar?.();
    if (esUltima) return onTerminar?.();
    return onSiguiente?.();
  };

  if (fase === 'espera') {
    return (
      <div className={`numerico numerico--${config.contenido}`} data-codigo={codigo}>
        <PanelEspera
          config={config}
          segundosRestantes={segundosRestantes}
          respondidas={respondidas}
          total={total}
          estadoGuardado={estadoGuardado}
          aviso={aviso}
          onRevisar={config.volverAtras ? onRevisar : undefined}
        />
      </div>
    );
  }

  return (
    <div className={`numerico numerico--${config.contenido}`} data-codigo={codigo}>
      <TestScreen
        testName={config.nombre}
        questionNumber={indice + 1}
        totalQuestions={total}
        questionLabel={pregunta ? config.etiqueta(pregunta.numero, total) : undefined}
        timeRemainingSeconds={fase === 'activa' ? segundosRestantes : null}
        prompt={prompt}
        promptVariant={config.contenido === 'problema' ? 'statement' : 'display'}
        options={opciones(pregunta, binaria)}
        optionsLayout={binaria ? 'binary' : config.contenido === 'ecuacion' ? 'compact' : 'list'}
        numeric
        selectedOption={seleccion}
        saveState={estadoGuardado}
        notice={aviso}
        phase={activa ? 'active' : fase === 'cerrada' ? 'closed' : 'instructions'}
        phaseTitle={fase === 'cerrada' ? 'Subprueba cerrada' : config.nombre}
        phaseDescription={
          fase === 'cerrada'
            ? `${cerradaPorTiempo ? 'El tiempo oficial terminó. ' : ''}Las respuestas guardadas quedaron registradas; esta subprueba ya no admite cambios ni se puede volver a ella.`
            : `${config.instrucciones} Tiempo oficial: ${config.tiempoOficial}. El tiempo empieza al pulsar «Comenzar».`
        }
        primaryActionLabel={etiquetaPrincipal}
        previousActionLabel="Anterior"
        onPrevious={activa && config.volverAtras && indice > 0 ? onAnterior : undefined}
        onSelect={activa ? onSeleccionar : undefined}
        onNext={alPrincipal}
      />
    </div>
  );
}

type PanelEsperaProps = {
  config: ConfigNumerica;
  segundosRestantes: number | null;
  respondidas: number;
  total: number;
  estadoGuardado: SaveState;
  aviso?: TestNoticeData;
  onRevisar?: () => void;
};

/** Espera hasta el cierre: mismo papel y reloj que `TestScreen`, sin alternativas ni avance. */
function PanelEspera({
  config,
  segundosRestantes,
  respondidas,
  total,
  estadoGuardado,
  aviso,
  onRevisar,
}: PanelEsperaProps) {
  const id = useId();
  const segundos = segundosRestantes === null ? undefined : Math.max(0, segundosRestantes);
  const que = config.respuesta === 'binaria' ? 'operaciones' : 'preguntas';
  return (
    <section className="test-paper test-paper--numeric" aria-labelledby={id}>
      <header className="test-header">
        <span className="test-name">{config.nombre}</span>
        <time
          className="test-timer"
          dateTime={segundos === undefined ? undefined : `PT${segundos}S`}
        >
          {formatearTiempo(segundosRestantes)}
        </time>
      </header>
      <div className="test-content">
        <section className="phase-panel num-espera">
          <p className="phase-label">En espera</p>
          <h1 className="question-prompt" id={id}>
            Terminaste esta subprueba
          </h1>
          <p className="phase-description">
            Respondiste {respondidas} de {total} {que}. Espera a que termine el tiempo oficial: la
            subprueba se cerrará sola y entonces podrás continuar. No se puede pasar a la siguiente
            antes del tiempo.
          </p>
          {onRevisar && (
            <p className="phase-description">
              Mientras quede tiempo puedes volver a revisar tus respuestas.
            </p>
          )}
          {aviso && <TestNotice {...aviso} />}
        </section>
      </div>
      <footer className="test-footer">
        <SaveStatus state={estadoGuardado} />
        <div className="test-actions">
          {onRevisar && (
            <button className="secondary-action" type="button" onClick={onRevisar}>
              Volver a revisar
            </button>
          )}
        </div>
      </footer>
    </section>
  );
}
