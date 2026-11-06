import { useEffect } from 'react';
import type { TestOption } from '../../../../components/AnswerOption';
import type { SaveState } from '../../../../components/SaveStatus';
import type { TestNoticeData } from '../../../../components/TestNotice';
import { TestScreen } from '../../../../components/TestScreen';
import type { PreguntaSubprueba } from '../../../../services/subpruebas';
import type { FormatoOperacion } from '../notacion';
import { ExpresionMatematica, TextoMatematico } from './Notacion';
import { CONFIG_NUMERICO, letraDeTecla, type CodigoNumerico } from '../subpruebas';
import '../numerico.css';

export type FaseNumerica = 'instrucciones' | 'activa' | 'cerrada';

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
  onIniciar: () => void;
  onSeleccionar: (letra: string) => void;
  onSiguiente?: () => void;
  onAnterior?: () => void;
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
  onIniciar,
  onSeleccionar,
  onSiguiente,
  onAnterior,
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

  const etiquetaPrincipal =
    fase === 'instrucciones' ? 'Comenzar' : fase === 'cerrada' ? 'Continuar' : 'Siguiente';

  const alPrincipal = () => {
    if (fase === 'instrucciones') return onIniciar();
    if (fase === 'cerrada') return onContinuar?.();
    return onSiguiente?.();
  };

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
