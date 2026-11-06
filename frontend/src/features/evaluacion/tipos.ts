/**
 * Contratos de GEN-02 (inicio y recuperación de evaluación).
 * Fuentes: Documento BFA §5.3 (E2, E3, E4) y Documento general §5.2.3 (E19, E21).
 * Los nombres siguen DOC-05; si DOC-03/DOC-05 cambian un campo, se ajusta aquí.
 */

// ---------- Enumeraciones ----------

export type EstadoEvaluacion = 'EN_CURSO' | 'FINALIZADA';

/** PENDIENTE → ACTIVA → CERRADA (E4, reglas). */
export type EstadoSubprueba = 'PENDIENTE' | 'ACTIVA' | 'CERRADA';

export type TipoRespuesta = 'SELECCION' | 'TEXTO_LIBRE';

// ---------- E19 — GET /sujetos/{sujetoId} ----------

export interface SujetoResponse {
  id: number;
  identificadorInstitucional: string;
  nombres: string;
  apellidos: string;
}

// ---------- E21 — GET /periodos?disponible=true ----------

export interface PeriodoResponse {
  id: number;
  codigo: string;
  denominacion: string;
  inicio: string | null;
  fin: string | null;
  /** Solo los disponibles admiten nuevas asignaciones (los que usa E3). */
  disponible: boolean;
}

// ---------- E2 — GET /tests/{testId} ----------

/** Configuración de una subprueba de la edición publicada. */
export interface ConfiguracionSubprueba {
  codigo: string;
  nombre: string;
  orden: number;
  factor: string;
  tiempoLimiteSegundos: number;
  tipoRespuesta: TipoRespuesta;
  opciones: number | null;
  numeroPreguntas: number;
  maximoEntradas: number | null;
  tienePractica: boolean;
}

export interface TestDetalleResponse {
  id: number;
  codigo: string;
  nombre: string;
  /**
   * Edición publicada que E3 exige. Pendiente en DOC-05 (ajuste propuesto §5.4.1):
   * el ejemplo actual de E2 aún no lo incluye.
   */
  versionFormularioId: number;
  subpruebas: ConfiguracionSubprueba[];
}

// ---------- E3 — POST /evaluaciones ----------

export interface IniciarEvaluacionRequest {
  /** Opcional; si se envía debe coincidir con la versión. */
  testId?: number;
  versionFormularioId: number;
  sujetoId: number;
  periodoId: number;
  /** Si se omite, la aplicación es completa; si se envía, solo esas en orden oficial. */
  subpruebas?: string[];
  /** Obligatorio si se incluye FV y no hay letra asignada previamente (RF-FV-06). */
  fluidez?: { letraInicial: string };
}

// ---------- E4 — GET /evaluaciones/{evaluacionId} (y respuesta 201 de E3) ----------

export interface SubpruebaActiva {
  codigo: string;
  finLimite: string;
  /** Lo calcula el servidor; el frontend no reinicia ni corrige el reloj. */
  segundosRestantes: number;
}

export interface EstadoSubpruebaResponse {
  codigo: string;
  estado: EstadoSubprueba;
}

export interface EvaluacionResponse {
  id: number;
  testId: number;
  /** Propuesto en DOC-05 §5.4.1; puede no venir todavía. */
  versionFormularioId?: number;
  sujetoId: number;
  periodoId: number;
  estado: EstadoEvaluacion;
  fechaInicio: string;
  fechaFin: string | null;
  subpruebaActiva: SubpruebaActiva | null;
  subpruebas: EstadoSubpruebaResponse[];
}

// ---------- Errores (Documento general §5.2.2) ----------

export type CodigoErrorEvaluacion =
  | 'SOLICITUD_INVALIDA'
  | 'NO_AUTENTICADO'
  | 'SIN_PERMISO'
  | 'TEST_NO_ENCONTRADO'
  | 'SUJETO_NO_ENCONTRADO'
  | 'PERIODO_NO_ENCONTRADO'
  | 'EVALUACION_NO_ENCONTRADA'
  | 'VERSION_NO_PUBLICADA'
  | 'EVALUACION_EN_CURSO'
  | 'LETRA_NO_ASIGNADA'
  | 'VERSION_INCOMPATIBLE'
  | 'SUBPRUEBA_NO_INCLUIDA'
  | 'ERROR_INTERNO';

/** Categoría visual del error; decide el tono y la acción sugerida en pantalla. */
export type TipoError =
  'validacion' | 'acceso' | 'no_encontrado' | 'conflicto' | 'conexion' | 'servidor';

export interface DetalleCampo {
  campo: string;
  mensaje: string;
}

/** Error ya traducido para la interfaz. */
export interface ErrorMostrable {
  tipo: TipoError;
  codigo: string;
  mensaje: string;
  detalles: DetalleCampo[];
}

// ---------- Puerto: lo implementan el servicio real y el mock ----------

export interface ClienteEvaluacion {
  obtenerTest(testId: number): Promise<TestDetalleResponse>;
  listarPeriodosDisponibles(): Promise<PeriodoResponse[]>;
  obtenerSujeto(sujetoId: number): Promise<SujetoResponse>;
  iniciarEvaluacion(solicitud: IniciarEvaluacionRequest): Promise<EvaluacionResponse>;
  obtenerEvaluacion(evaluacionId: number): Promise<EvaluacionResponse>;
}

// ---------- Estado de una petición en la interfaz ----------

/**
 * inicial → cargando → (exito | error). Los datos oficiales solo existen en `exito`,
 * es decir, cuando el servidor (o el mock) confirmó la operación.
 */
export type EstadoPeticion<T> =
  | { fase: 'inicial' }
  | { fase: 'cargando' }
  | { fase: 'error'; error: ErrorMostrable }
  | { fase: 'exito'; datos: T };
