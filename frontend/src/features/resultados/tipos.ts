/**
 * Contrato de E9 — GET /api/v1/evaluaciones/{evaluacionId}/resultados.
 * Fuentes: Documento BFA §5.3 (E9) y §5.4.1 (ajuste de E9); Documento general ENT-COM-10;
 * Documento BFA ENT-BFA-10 (estados por factor). Los nombres siguen DOC-05; si cambian, se ajusta aquí.
 *
 * El frontend solo presenta lo que devuelve el backend: no calcula puntuaciones,
 * percentiles ni compuestos (Documento BFA §6) y no conoce reglas de un test concreto.
 */

/** ENT-COM-10. Independiente del estado de la evaluación. */
export type EstadoResultado = 'CALCULANDO' | 'PENDIENTE_VALIDACION' | 'DISPONIBLE';

/** NO_CALCULABLE ≠ cero: faltan componentes (aplicación parcial o insumos sin aprobar). */
export type EstadoCalculo = 'CALCULADO' | 'NO_CALCULABLE' | 'PENDIENTE_VALIDACION';

/** CONVERTIDO exige un rango exacto de un baremo aprobado; sin coincidencia no se interpola. */
export type EstadoNormativo =
  'CONVERTIDO' | 'SIN_COINCIDENCIA' | 'SIN_BAREMO_VALIDADO' | 'NO_APLICA';

export interface ResultadoFactor {
  codigo: string;
  /** Nula si el factor no es calculable. */
  puntuacionDirecta: number | null;
  estadoCalculo: EstadoCalculo;
  /** Solo existe con un rango exacto de baremo aprobado; si no, nulo con estado y motivo. */
  percentil: number | null;
  idRango: number | null;
  estadoNormativo: EstadoNormativo;
  /** Obligatorio cuando no se calcula o no se convierte. */
  motivo: string | null;
}

export interface ResultadoResponse {
  evaluacionId: number;
  resultadoId: number;
  estadoResultado: EstadoResultado;
  versionFormularioId: number | null;
  /** Nulos sin revisión publicada. */
  calificacionId: number | null;
  revision: number | null;
  /** Identificadores de los insumos; nunca el contenido de la clave. */
  versionReglas: string | number | null;
  idClave: number | null;
  idBaremo: number | null;
  /** Vacío sin revisión publicada. */
  factores: ResultadoFactor[];
}

/** Puerto: lo implementan el servicio real y el mock. */
export interface ClienteResultados {
  obtenerResultado(evaluacionId: number): Promise<ResultadoResponse>;
}
