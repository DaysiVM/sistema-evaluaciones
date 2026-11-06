import { useCallback, useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { useSearchParams } from 'react-router';
import type { SaveState } from '../components/SaveStatus';
import type { TestNoticeData } from '../components/TestNotice';
import {
  AvanceFamilia,
  type EstadoEnFamilia,
} from '../features/bfa/numerico/componentes/AvanceFamilia';
import { FinFamilia, type ResumenSubprueba } from '../features/bfa/numerico/componentes/FinFamilia';
import {
  PantallaNumerica,
  type FaseNumerica,
} from '../features/bfa/numerico/componentes/PantallaNumerica';
import type { FormatoOperacion } from '../features/bfa/numerico/notacion';
import {
  CODIGOS_NUMERICO,
  CONFIG_NUMERICO,
  esCodigoNumerico,
  siguienteSubprueba,
  type CodigoNumerico,
} from '../features/bfa/numerico/subpruebas';
import { ApiError } from '../services/http';
import { ERRORES_SUBPRUEBA, type PreguntaSubprueba } from '../services/subpruebas';
import { crearClienteSubpruebasMock } from '../services/subpruebas.mock';

/**
 * VISTA PREVIA de BFA-FE-06: recorrido N1-A → N1-B → N2-A → N2-B con el mock de E5/E6/E7.
 * La orquestación de aquí es mínima y solo sirve para revisar las pantallas: la definitiva es el
 * motor común de GEN-03-FE (PR #22) y el flujo general del BFA.
 *
 *   /dev/bfa-fe-06                    recorrido completo desde Sumas
 *   ?desde=N2-A                       empieza en otra subprueba
 *   &formato=vertical                 Sumas y Multiplicaciones en columna
 *   &duracion=20                      tiempo corto para revisar el cierre
 *   &evaluacion=503                   el guardado falla por red
 */

const CIERRE = new Set<string>([ERRORES_SUBPRUEBA.tiempoAgotado, ERRORES_SUBPRUEBA.noActiva]);

function estadoGlobal(guardados: Record<number, SaveState>): SaveState {
  const valores = Object.values(guardados);
  if (valores.includes('error')) return 'error';
  if (valores.includes('saving')) return 'saving';
  return 'saved';
}

function estadosIniciales(desde: CodigoNumerico) {
  const posicion = CODIGOS_NUMERICO.indexOf(desde);
  return Object.fromEntries(
    CODIGOS_NUMERICO.map((c, i) => [c, i < posicion ? 'cerrada' : 'pendiente']),
  ) as Record<CodigoNumerico, EstadoEnFamilia>;
}

export function BfaFe06Preview() {
  const [params] = useSearchParams();
  const pedido = params.get('desde') ?? params.get('subprueba') ?? 'N1-A';
  const desde: CodigoNumerico = esCodigoNumerico(pedido) ? pedido : 'N1-A';
  const formato: FormatoOperacion =
    params.get('formato') === 'vertical' ? 'vertical' : 'horizontal';
  const duracion = Number(params.get('duracion')) || undefined;
  const evaluacionId = Number(params.get('evaluacion')) || 1;
  const [vuelta, setVuelta] = useState(0);

  // Reloj compartido por el mock y la pantalla; «Simular fin del tiempo» lo adelanta.
  const desfase = useRef(0);
  const reloj = useCallback(() => Date.now() + desfase.current, []);

  const cliente = useMemo(
    () =>
      crearClienteSubpruebasMock({
        duracionSegundos: duracion,
        ahora: reloj,
        // Si se empieza en medio de la familia, las anteriores no existen en el mock.
        validarOrden: desde === 'N1-A',
      }),
    // `vuelta` reinicia el recorrido con un mock nuevo.
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [duracion, evaluacionId, desde, vuelta, reloj],
  );

  const [codigo, setCodigo] = useState<CodigoNumerico | null>(desde);
  const [estados, setEstados] = useState(() => estadosIniciales(desde));
  const [resumen, setResumen] = useState<Partial<Record<CodigoNumerico, ResumenSubprueba>>>({});

  const [fase, setFase] = useState<FaseNumerica>('instrucciones');
  const [preguntas, setPreguntas] = useState<PreguntaSubprueba[]>([]);
  const [indice, setIndice] = useState(0);
  const [selecciones, setSelecciones] = useState<Record<number, string>>({});
  const [guardados, setGuardados] = useState<Record<number, SaveState>>({});
  const [limite, setLimite] = useState<number | null>(null);
  const [ahora, setAhora] = useState(reloj);
  const [aviso, setAviso] = useState<TestNoticeData | undefined>();
  const [porTiempo, setPorTiempo] = useState(false);
  const [cambioMs, setCambioMs] = useState<number | null>(null);
  const marcaCambio = useRef<number | null>(null);

  const prepararSubprueba = useCallback((siguiente: CodigoNumerico | null) => {
    setCodigo(siguiente);
    setFase('instrucciones');
    setPreguntas([]);
    setIndice(0);
    setSelecciones({});
    setGuardados({});
    setLimite(null);
    setAviso(undefined);
    setPorTiempo(false);
    setCambioMs(null);
  }, []);

  // Recorrido nuevo cuando cambia la configuración de la vista previa.
  useEffect(() => {
    desfase.current = 0;
    setEstados(estadosIniciales(desde));
    setResumen({});
    prepararSubprueba(desde);
  }, [cliente, desde, prepararSubprueba]);

  const cerrar = useCallback((tiempo: boolean) => {
    setFase('cerrada');
    setLimite(null);
    setPorTiempo(tiempo);
  }, []);

  // El reloj solo cuenta desde el último tiempo informado por el backend.
  useEffect(() => {
    if ((fase !== 'activa' && fase !== 'espera') || limite === null) return;
    const latido = () => {
      const t = reloj();
      setAhora(t);
      if (t >= limite) cerrar(true);
    };
    latido();
    const id = setInterval(latido, 250);
    return () => clearInterval(id);
  }, [fase, limite, cerrar, reloj]);

  // Tiempo real entre la respuesta y la pantalla siguiente ya dibujada.
  useEffect(() => {
    if (marcaCambio.current === null) return;
    const inicio = marcaCambio.current;
    marcaCambio.current = null;
    requestAnimationFrame(() => setCambioMs(Math.round(performance.now() - inicio)));
  }, [indice]);

  // Al cerrarse, la subprueba queda marcada y se guarda cuántas respuestas se registraron.
  useEffect(() => {
    if (fase !== 'cerrada' || !codigo) return;
    setEstados((e) => ({ ...e, [codigo]: 'cerrada' }));
    const respondidas = Object.values(guardados).filter((g) => g === 'saved').length;
    setResumen((r) => ({ ...r, [codigo]: { respondidas, total: preguntas.length } }));
  }, [fase, codigo, guardados, preguntas.length]);

  if (!codigo) {
    return (
      <Pagina estados={estados}>
        <FinFamilia resumen={resumen} />
        <div className="num-dev" role="note">
          <strong>Vista previa</strong>
          <span>En la batería seguiría Fluidez Verbal (otra tarea).</span>
          <button
            className="secondary-action"
            type="button"
            onClick={() => setVuelta((v) => v + 1)}
          >
            Repetir el recorrido
          </button>
        </div>
      </Pagina>
    );
  }

  const config = CONFIG_NUMERICO[codigo];
  const pregunta = preguntas[indice];
  const respondidas = Object.values(guardados).filter((g) => g === 'saved').length;

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
      setLimite(reloj() + Math.min(activa.segundosRestantes, e6.segundosRestantes) * 1000);
      setEstados((e) => ({ ...e, [codigo]: 'en-curso' }));
      setAviso(undefined);
      setFase('activa');
    } catch (error) {
      setAviso({
        kind: 'error',
        message: error instanceof ApiError ? error.message : 'No se pudo iniciar la subprueba.',
      });
    }
  };

  const guardar = async (p: PreguntaSubprueba, letra: string) => {
    setGuardados((g) => ({ ...g, [p.id]: 'saving' }));
    try {
      const avance = await cliente.registrarRespuestas(evaluacionId, codigo, [
        { preguntaId: p.id, opcion: letra },
      ]);
      setGuardados((g) => ({ ...g, [p.id]: 'saved' }));
      setLimite(reloj() + avance.segundosRestantes * 1000);
    } catch (error) {
      setGuardados((g) => ({ ...g, [p.id]: 'error' }));
      if (error instanceof ApiError && CIERRE.has(error.error?.codigo ?? '')) {
        cerrar(error.error?.codigo === ERRORES_SUBPRUEBA.tiempoAgotado);
        return;
      }
      setAviso({
        kind: 'error',
        message: `No se pudo guardar la respuesta de la pregunta ${p.numero}.`,
      });
    }
  };

  const seleccionar = (letra: string) => {
    if (fase !== 'activa' || !pregunta) return;
    setAviso(undefined);
    setSelecciones((s) => ({ ...s, [pregunta.id]: letra }));
    void guardar(pregunta, letra);
    if (config.respuesta !== 'binaria') return;
    // Sumas y Multiplicaciones avanzan de inmediato; el guardado sigue en segundo plano.
    if (indice < preguntas.length - 1) {
      marcaCambio.current = performance.now();
      setIndice(indice + 1);
    } else {
      setFase('espera');
    }
  };

  const continuar = () => prepararSubprueba(siguienteSubprueba(codigo));

  const simularFin = () => {
    if (limite === null) return;
    desfase.current += Math.max(0, limite - reloj());
    setAhora(reloj());
  };

  const restantes = limite === null ? null : Math.max(0, Math.ceil((limite - ahora) / 1000));

  return (
    <Pagina estados={estados}>
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
        respondidas={respondidas}
        onIniciar={() => void iniciar()}
        onSeleccionar={seleccionar}
        onSiguiente={() => {
          marcaCambio.current = performance.now();
          setIndice((i) => Math.min(i + 1, preguntas.length - 1));
        }}
        onAnterior={() => setIndice((i) => Math.max(0, i - 1))}
        onTerminar={() => setFase('espera')}
        onRevisar={() => setFase('activa')}
        onContinuar={continuar}
      />

      <div className="num-dev" role="note">
        <strong>Vista previa</strong>
        <span>Preguntas de ejemplo, no son reactivos del BFA.</span>
        {cambioMs !== null && (
          <span>
            Último cambio de pregunta: <strong>{cambioMs} ms</strong> (umbral: menos de 100 ms)
          </span>
        )}
        {(fase === 'activa' || fase === 'espera') && (
          <button className="secondary-action" type="button" onClick={simularFin}>
            Simular fin del tiempo
          </button>
        )}
      </div>
    </Pagina>
  );
}

function Pagina({
  estados,
  children,
}: {
  estados: Record<CodigoNumerico, EstadoEnFamilia>;
  children: ReactNode;
}) {
  return (
    <div className="num-pagina">
      <header className="num-pagina-cabecera">
        <h1>Batería Factorial de Aptitudes · Sección numérica</h1>
        <p>
          Cuatro subpruebas en el orden oficial. Cada una tiene su propio tiempo y, al cerrarse, no
          se puede volver a ella.
        </p>
      </header>
      <AvanceFamilia estados={estados} />
      <div className="num-panel">{children}</div>
    </div>
  );
}
