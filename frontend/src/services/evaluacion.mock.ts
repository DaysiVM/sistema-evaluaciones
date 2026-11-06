import type {
  ClienteEvaluacion,
  ConfiguracionSubprueba,
  DetalleCampo,
  EvaluacionResponse,
  IniciarEvaluacionRequest,
  PeriodoResponse,
  SujetoResponse,
  TestDetalleResponse,
} from '../features/evaluacion/tipos';
import { ApiError } from './http';

/**
 * Simulación de E2, E3, E4, E19 y E21 con los datos ficticios de DOC-05.
 * Cumple el mismo contrato que services/evaluacion.ts para cambiarla sin tocar la interfaz.
 *
 * Escenarios por valor de prueba:
 *   Sujeto 25 / 26        → existen
 *   Sujeto 409            → existe, pero E3 responde EVALUACION_EN_CURSO
 *   Sujeto 403 / 500      → SIN_PERMISO / ERROR_INTERNO
 *   Sujeto 503            → fallo de conexión
 *   Cualquier otro sujeto → SUJETO_NO_ENCONTRADO
 *   Evaluación 15 / 16    → en curso (VOC2 activa) / finalizada
 *   Evaluación 403 / 500 / 503 → SIN_PERMISO / ERROR_INTERNO / fallo de conexión
 *   Incluir FV sin letra inicial → LETRA_NO_ASIGNADA
 */

const VERSION_PUBLICADA = 10;

const subprueba = (
  orden: number,
  codigo: string,
  nombre: string,
  factor: string,
  tiempoLimiteSegundos: number,
  opciones: number | null,
  numeroPreguntas: number,
): ConfiguracionSubprueba => ({
  codigo,
  nombre,
  orden,
  factor,
  tiempoLimiteSegundos,
  tipoRespuesta: 'SELECCION',
  opciones,
  numeroPreguntas,
  maximoEntradas: null,
  tienePractica: true,
});

/**
 * Documento BFA §5.3, tabla de las 13 subpruebas (y §2.6 para tiempos).
 * 0 preguntas u opciones null = no definido en la fuente.
 */
const TEST_BFA: TestDetalleResponse = {
  id: 1,
  codigo: 'BFA',
  nombre: 'Batería Factorial de Aptitudes',
  versionFormularioId: VERSION_PUBLICADA,
  subpruebas: [
    subprueba(1, 'VOC1', 'Vocabulario - Forma A', 'VOC1', 300, 5, 37),
    subprueba(2, 'VOC2', 'Vocabulario - Forma B', 'VOC2', 360, 5, 37),
    subprueba(3, 'S1', 'Espacial - Figuras Idénticas', 'S1', 180, null, 0),
    subprueba(4, 'S2', 'Espacial - Desplazamiento', 'S2', 300, null, 0),
    subprueba(5, 'S1b', 'Espacial - Ladrillos-Cubos', 'S1', 210, 5, 0),
    subprueba(6, 'R1', 'Razonamiento - Forma A', 'R1', 600, 4, 20),
    subprueba(7, 'R2', 'Razonamiento - Forma B', 'R2', 720, 4, 0),
    subprueba(8, 'CV', 'Comprensión Verbal', 'CV', 900, 3, 31),
    subprueba(9, 'N1-A', 'Numérico - Sumas', 'N1', 240, 2, 0),
    subprueba(10, 'N1-B', 'Numérico - Multiplicaciones', 'N1', 240, 2, 0),
    subprueba(11, 'N2-A', 'Numérico - Operaciones', 'N2', 360, 4, 0),
    subprueba(12, 'N2-B', 'Numérico - Problemas', 'N2', 360, 4, 0),
    {
      codigo: 'FV',
      nombre: 'Fluidez Verbal',
      orden: 13,
      factor: 'FV',
      tiempoLimiteSegundos: 300,
      tipoRespuesta: 'TEXTO_LIBRE',
      opciones: null,
      numeroPreguntas: 0,
      maximoEntradas: 75,
      tienePractica: true,
    },
  ],
};

const SUJETOS: SujetoResponse[] = [
  { id: 25, identificadorInstitucional: '2026-0153', nombres: 'Ana', apellidos: 'Ruiz' },
  { id: 26, identificadorInstitucional: '2026-0154', nombres: 'Luis', apellidos: 'Pérez' },
  { id: 409, identificadorInstitucional: '2026-0409', nombres: 'Marta', apellidos: 'López' },
];

const PERIODOS: PeriodoResponse[] = [
  {
    id: 3,
    codigo: '2026-2',
    denominacion: 'Segundo semestre 2026',
    inicio: '2026-08-01',
    fin: '2026-12-15',
    disponible: true,
  },
  {
    id: 4,
    codigo: '2027-1',
    denominacion: 'Primer semestre 2027',
    inicio: '2027-01-15',
    fin: '2027-06-30',
    disponible: true,
  },
  {
    id: 2,
    codigo: '2026-1',
    denominacion: 'Primer semestre 2026',
    inicio: '2026-01-15',
    fin: '2026-06-30',
    disponible: false,
  },
];

const evaluacionesIniciales = (): EvaluacionResponse[] => [
  {
    id: 15,
    testId: 1,
    versionFormularioId: VERSION_PUBLICADA,
    sujetoId: 25,
    periodoId: 3,
    estado: 'EN_CURSO',
    fechaInicio: '2026-10-30T15:30:00Z',
    fechaFin: null,
    subpruebaActiva: { codigo: 'VOC2', finLimite: '2026-10-30T15:42:10Z', segundosRestantes: 214 },
    subpruebas: [
      { codigo: 'VOC1', estado: 'CERRADA' },
      { codigo: 'VOC2', estado: 'ACTIVA' },
      { codigo: 'CV', estado: 'PENDIENTE' },
    ],
  },
  {
    id: 16,
    testId: 1,
    versionFormularioId: VERSION_PUBLICADA,
    sujetoId: 26,
    periodoId: 3,
    estado: 'FINALIZADA',
    fechaInicio: '2026-10-29T14:00:00Z',
    fechaFin: '2026-10-29T15:10:00Z',
    subpruebaActiva: null,
    subpruebas: [
      { codigo: 'VOC1', estado: 'CERRADA' },
      { codigo: 'R1', estado: 'CERRADA' },
    ],
  },
];

// ---------- Ayudas ----------

function fallar(
  status: number,
  codigo: string,
  mensaje: string,
  ruta: string,
  detalles?: DetalleCampo[],
): never {
  throw new ApiError(status, {
    timestamp: new Date().toISOString(),
    status,
    codigo,
    mensaje,
    ruta,
    ...(detalles ? { detalles } : {}),
  });
}

/** Igual que fetch cuando no hay red: TypeError. */
function fallarConexion(): never {
  throw new TypeError('Failed to fetch');
}

/** Escenarios comunes por ID de prueba (403, 500, 503). */
function escenarioPorId(id: number, ruta: string): void {
  if (id === 503) fallarConexion();
  if (id === 403) fallar(403, 'SIN_PERMISO', 'No tiene permiso para este recurso.', ruta);
  if (id === 500) fallar(500, 'ERROR_INTERNO', 'Ocurrió un error inesperado.', ruta);
}

const esIdValido = (valor: unknown): valor is number =>
  typeof valor === 'number' && Number.isInteger(valor) && valor > 0;

const copiar = <T>(valor: T): T => structuredClone(valor);

// ---------- Cliente simulado ----------

export interface OpcionesMock {
  /** Retardo artificial en ms (0 en pruebas). */
  retardoMs?: number;
}

export function crearClienteMock({ retardoMs = 700 }: OpcionesMock = {}): ClienteEvaluacion {
  const evaluaciones = new Map<number, EvaluacionResponse>(
    evaluacionesIniciales().map((e) => [e.id, e]),
  );
  let siguienteId = 100;

  const esperar = () => new Promise<void>((resolver) => setTimeout(resolver, retardoMs));

  return {
    async obtenerTest(testId) {
      await esperar();
      const ruta = `/api/v1/tests/${testId}`;
      if (testId !== TEST_BFA.id) fallar(404, 'TEST_NO_ENCONTRADO', 'El test no existe.', ruta);
      return copiar(TEST_BFA);
    },

    async listarPeriodosDisponibles() {
      await esperar();
      return copiar(PERIODOS.filter((p) => p.disponible));
    },

    async obtenerSujeto(sujetoId) {
      await esperar();
      const ruta = `/api/v1/sujetos/${sujetoId}`;
      escenarioPorId(sujetoId, ruta);
      const sujeto = SUJETOS.find((s) => s.id === sujetoId);
      if (!sujeto) fallar(404, 'SUJETO_NO_ENCONTRADO', `El sujeto ${sujetoId} no existe.`, ruta);
      return copiar(sujeto);
    },

    async iniciarEvaluacion(solicitud: IniciarEvaluacionRequest) {
      await esperar();
      const ruta = '/api/v1/evaluaciones';

      // 400: campos obligatorios y tipos.
      const detalles: DetalleCampo[] = [];
      if (!esIdValido(solicitud.sujetoId))
        detalles.push({ campo: 'sujetoId', mensaje: 'es obligatorio' });
      if (!esIdValido(solicitud.periodoId))
        detalles.push({ campo: 'periodoId', mensaje: 'es obligatorio' });
      if (!esIdValido(solicitud.versionFormularioId))
        detalles.push({ campo: 'versionFormularioId', mensaje: 'es obligatorio' });
      const codigos = solicitud.subpruebas ?? [];
      if (new Set(codigos).size !== codigos.length)
        detalles.push({ campo: 'subpruebas', mensaje: 'no admite duplicados' });
      if (solicitud.subpruebas !== undefined && codigos.length === 0)
        detalles.push({ campo: 'subpruebas', mensaje: 'debe incluir al menos una subprueba' });
      if (detalles.length > 0)
        fallar(400, 'SOLICITUD_INVALIDA', 'La solicitud tiene datos inválidos.', ruta, detalles);

      // Escenarios por sujeto y existencia de recursos.
      escenarioPorId(solicitud.sujetoId, ruta);
      if (!SUJETOS.some((s) => s.id === solicitud.sujetoId))
        fallar(404, 'SUJETO_NO_ENCONTRADO', `El sujeto ${solicitud.sujetoId} no existe.`, ruta);
      if (!PERIODOS.some((p) => p.id === solicitud.periodoId))
        fallar(404, 'PERIODO_NO_ENCONTRADO', `El período ${solicitud.periodoId} no existe.`, ruta);
      if (solicitud.testId !== undefined && solicitud.testId !== TEST_BFA.id)
        fallar(422, 'VERSION_INCOMPATIBLE', 'El test no coincide con la versión.', ruta);
      if (solicitud.versionFormularioId !== VERSION_PUBLICADA)
        fallar(409, 'VERSION_NO_PUBLICADA', 'La versión del formulario no está publicada.', ruta);

      const validos = new Set(TEST_BFA.subpruebas.map((s) => s.codigo));
      const ajena = codigos.find((c) => !validos.has(c));
      if (ajena)
        fallar(
          422,
          'SUBPRUEBA_NO_INCLUIDA',
          `La subprueba ${ajena} no pertenece a la versión.`,
          ruta,
        );

      // 409: unicidad sujeto + test + período (DOC-04 CU-GEN-04).
      const abierta = [...evaluaciones.values()].some(
        (e) =>
          e.estado === 'EN_CURSO' &&
          e.sujetoId === solicitud.sujetoId &&
          e.periodoId === solicitud.periodoId &&
          e.testId === TEST_BFA.id,
      );
      if (solicitud.sujetoId === 409 || abierta)
        fallar(
          409,
          'EVALUACION_EN_CURSO',
          'El sujeto ya tiene una evaluación abierta de este test en este período.',
          ruta,
        );

      // Orden oficial; si se omite subpruebas, la aplicación es completa.
      const incluidas = TEST_BFA.subpruebas
        .filter((s) => solicitud.subpruebas === undefined || codigos.includes(s.codigo))
        .map((s) => s.codigo);

      if (incluidas.includes('FV') && !solicitud.fluidez?.letraInicial?.trim())
        fallar(409, 'LETRA_NO_ASIGNADA', 'Falta la letra inicial de Fluidez Verbal.', ruta);

      const creada: EvaluacionResponse = {
        id: siguienteId++,
        testId: TEST_BFA.id,
        versionFormularioId: solicitud.versionFormularioId,
        sujetoId: solicitud.sujetoId,
        periodoId: solicitud.periodoId,
        estado: 'EN_CURSO',
        fechaInicio: new Date().toISOString(),
        fechaFin: null,
        subpruebaActiva: null,
        subpruebas: incluidas.map((codigo) => ({ codigo, estado: 'PENDIENTE' })),
      };
      evaluaciones.set(creada.id, creada);
      return copiar(creada);
    },

    async obtenerEvaluacion(evaluacionId) {
      await esperar();
      const ruta = `/api/v1/evaluaciones/${evaluacionId}`;
      escenarioPorId(evaluacionId, ruta);
      const evaluacion = evaluaciones.get(evaluacionId);
      if (!evaluacion)
        fallar(404, 'EVALUACION_NO_ENCONTRADA', `La evaluación ${evaluacionId} no existe.`, ruta);
      return copiar(evaluacion);
    },
  };
}
