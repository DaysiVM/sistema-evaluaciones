import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useSearchParams } from 'react-router';
import type { SaveState } from '../components/SaveStatus';
import type { TestNoticeData } from '../components/TestNotice';
import {
  PantallaNumerica,
  type FaseNumerica,
} from '../features/bfa/numerico/componentes/PantallaNumerica';
import type { FormatoOperacion } from '../features/bfa/numerico/notacion';
import {
  CODIGOS_NUMERICO,
  CONFIG_NUMERICO,
  esCodigoNumerico,
} from '../features/bfa/numerico/subpruebas';
import { ApiError } from '../services/http';
import { ERRORES_SUBPRUEBA, type PreguntaSubprueba } from '../services/subpruebas';
import { crearClienteSubpruebasMock } from '../services/subpruebas.mock';

/**
 * VISTA PREVIA de BFA-FE-06 con el mock de E5/E6/E7. La orquestación de aquí es mínima y solo sirve
 * para revisar las pantallas: la definitiva es el motor común de GEN-03-FE (PR #22).
 *
 *   /dev/bfa-fe-06?subprueba=N1-A&formato=vertical&duracion=20&evaluacion=503
 */

type Guardado = SaveState;

const CIERRE = new Set<string>([ERRORES_SUBPRUEBA.tiempoAgotado, ERRORES_SUBPRUEBA.noActiva]);

function estadoGlobal(guardados: Record<number, Guardado>): SaveState {
  const valores = Object.values(guardados);
  if (valores.includes('error')) return 'error';
  if (valores.includes('saving')) return 'saving';
  return 'saved';
}

export function BfaFe06Preview() {
  const [params, setParams] = useSearchParams();
  const pedido = params.get('subprueba') ?? 'N1-A';
  const codigo = esCodigoNumerico(pedido) ? pedido : 'N1-A';
  const formato: FormatoOperacion =
    params.get('formato') === 'vertical' ? 'vertical' : 'horizontal';
  const duracion = Number(params.get('duracion')) || undefined;
  const evaluacionId = Number(params.get('evaluacion')) || 1;

  const cliente = useMemo(
    () => crearClienteSubpruebasMock({ duracionSegundos: duracion, validarOrden: false }),
    // Un cliente nuevo por subprueba: cada revisión empieza en PENDIENTE.
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [codigo, duracion, evaluacionId],
  );

  const [fase, setFase] = useState<FaseNumerica>('instrucciones');
  const [preguntas, setPreguntas] = useState<PreguntaSubprueba[]>([]);
  const [indice, setIndice] = useState(0);
  const [selecciones, setSelecciones] = useState<Record<number, string>>({});
  const [guardados, setGuardados] = useState<Record<number, Guardado>>({});
  const [limite, setLimite] = useState<number | null>(null);
  const [ahora, setAhora] = useState(() => Date.now());
  const [aviso, setAviso] = useState<TestNoticeData | undefined>();
  const [porTiempo, setPorTiempo] = useState(false);
  const [cambioMs, setCambioMs] = useState<number | null>(null);
  const marcaCambio = useRef<number | null>(null);

  useEffect(() => {
    setFase('instrucciones');
    setPreguntas([]);
    setIndice(0);
    setSelecciones({});
    setGuardados({});
    setLimite(null);
    setAviso(undefined);
    setPorTiempo(false);
  }, [cliente]);

  const cerrar = useCallback((tiempo: boolean) => {
    setFase('cerrada');
    setLimite(null);
    setPorTiempo(tiempo);
  }, []);

  // Reloj local que solo cuenta desde el último tiempo informado por el backend.
  useEffect(() => {
    if (fase !== 'activa' || limite === null) return;
    const latido = () => {
      const t = Date.now();
      setAhora(t);
      if (t >= limite) cerrar(true);
    };
    latido();
    const id = setInterval(latido, 250);
    return () => clearInterval(id);
  }, [fase, limite, cerrar]);

  // Tiempo real entre la respuesta y la pantalla siguiente ya dibujada.
  useEffect(() => {
    if (marcaCambio.current === null) return;
    const inicio = marcaCambio.current;
    marcaCambio.current = null;
    requestAnimationFrame(() => setCambioMs(Math.round(performance.now() - inicio)));
  }, [indice]);

  const iniciar = async () => {
    try {
      const activa = await cliente.iniciar(evaluacionId, codigo);
      const e6 = await cliente.obtenerPreguntas(evaluacionId, codigo);
      setPreguntas(e6.preguntas);
      const marcadas: Record<number, string> = {};
      e6.preguntas.forEach((p) => {
        if (p.respuestaActual) marcadas[p.id] = p.respuestaActual;
      });
      setSelecciones(marcadas);
      const primeraLibre = e6.preguntas.findIndex((p) => !p.respuestaActual);
      setIndice(primeraLibre === -1 ? 0 : primeraLibre);
      setLimite(Date.now() + Math.min(activa.segundosRestantes, e6.segundosRestantes) * 1000);
      setFase('activa');
    } catch (error) {
      setAviso({
        kind: 'error',
        message: error instanceof ApiError ? error.message : 'No se pudo iniciar la subprueba.',
      });
    }
  };

  const guardar = async (pregunta: PreguntaSubprueba, letra: string) => {
    setGuardados((g) => ({ ...g, [pregunta.id]: 'saving' }));
    try {
      const avance = await cliente.registrarRespuestas(evaluacionId, codigo, [
        { preguntaId: pregunta.id, opcion: letra },
      ]);
      setGuardados((g) => ({ ...g, [pregunta.id]: 'saved' }));
      setLimite(Date.now() + avance.segundosRestantes * 1000);
    } catch (error) {
      if (error instanceof ApiError && CIERRE.has(error.error?.codigo ?? '')) {
        setGuardados((g) => ({ ...g, [pregunta.id]: 'error' }));
        cerrar(error.error?.codigo === ERRORES_SUBPRUEBA.tiempoAgotado);
        return;
      }
      setGuardados((g) => ({ ...g, [pregunta.id]: 'error' }));
      setAviso({
        kind: 'error',
        message: `No se pudo guardar la respuesta de la pregunta ${pregunta.numero}.`,
      });
    }
  };

  const pregunta = preguntas[indice];
  const config = CONFIG_NUMERICO[codigo];

  const seleccionar = (letra: string) => {
    if (fase !== 'activa' || !pregunta) return;
    setAviso(undefined);
    setSelecciones((s) => ({ ...s, [pregunta.id]: letra }));
    void guardar(pregunta, letra);
    // En Sumas y Multiplicaciones se pasa de inmediato: el guardado sigue en segundo plano.
    if (config.respuesta === 'binaria' && indice < preguntas.length - 1) {
      marcaCambio.current = performance.now();
      setIndice(indice + 1);
    } else if (config.respuesta === 'binaria') {
      setAviso({
        kind: 'warning',
        message: 'Respondiste todas las operaciones. Espera el cierre.',
      });
    }
  };

  const restantes = limite === null ? null : Math.max(0, Math.ceil((limite - ahora) / 1000));

  return (
    <div className="app">
      <nav aria-label="Subpruebas de la vista previa" style={{ marginBottom: '1rem' }}>
        {CODIGOS_NUMERICO.map((c) => (
          <button
            key={c}
            type="button"
            className={c === codigo ? 'primary-action' : 'secondary-action'}
            style={{ marginRight: '0.5rem' }}
            onClick={() => setParams((p) => ({ ...Object.fromEntries(p), subprueba: c }))}
          >
            {c}
          </button>
        ))}
      </nav>

      <PantallaNumerica
        codigo={codigo}
        fase={fase}
        pregunta={fase === 'activa' ? pregunta : undefined}
        indice={indice}
        total={preguntas.length}
        seleccion={pregunta ? (selecciones[pregunta.id] ?? null) : null}
        estadoGuardado={estadoGlobal(guardados)}
        segundosRestantes={restantes}
        aviso={aviso}
        formato={formato}
        cerradaPorTiempo={porTiempo}
        onIniciar={() => void iniciar()}
        onSeleccionar={seleccionar}
        onSiguiente={() => {
          marcaCambio.current = performance.now();
          setIndice((i) => Math.min(i + 1, preguntas.length - 1));
        }}
        onAnterior={() => setIndice((i) => Math.max(0, i - 1))}
      />

      <p className="phase-description" role="note" style={{ marginTop: '1rem' }}>
        Vista previa con datos de ejemplo (no son reactivos del BFA).
        {cambioMs !== null &&
          ` Último cambio de pregunta: ${cambioMs} ms (umbral: menos de 100 ms).`}
      </p>
    </div>
  );
}
