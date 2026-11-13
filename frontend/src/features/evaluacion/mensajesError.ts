import { ApiError } from '../../services/http';
import type { CodigoErrorEvaluacion, ErrorMostrable, TipoError } from './tipos';

/** Texto para el usuario por código estable del backend (Documento general §5.2.2). */
const MENSAJES: Record<CodigoErrorEvaluacion, string> = {
  SOLICITUD_INVALIDA: 'Revise los datos del formulario.',
  NO_AUTENTICADO: 'Su sesión no es válida. Inicie sesión de nuevo.',
  SIN_PERMISO: 'No tiene permiso para realizar esta acción o ver esta evaluación.',
  TEST_NO_ENCONTRADO: 'El test seleccionado no existe o no está activo.',
  SUJETO_NO_ENCONTRADO: 'No existe un evaluado con ese ID. Debe registrarse antes de evaluarlo.',
  PERIODO_NO_ENCONTRADO: 'El período seleccionado no existe.',
  EVALUACION_NO_ENCONTRADA: 'No existe una evaluación con ese ID.',
  VERSION_NO_PUBLICADA: 'La versión del test no está publicada; no se puede iniciar.',
  EVALUACION_EN_CURSO:
    'El evaluado ya tiene una evaluación abierta de este test en ese período. Retómela en lugar de iniciar otra.',
  LETRA_NO_ASIGNADA: 'Fluidez Verbal necesita una letra inicial asignada.',
  VERSION_INCOMPATIBLE: 'El test no coincide con la versión del formulario.',
  SUBPRUEBA_NO_INCLUIDA: 'Alguna subprueba elegida no pertenece a esta versión del test.',
  ERROR_INTERNO: 'Ocurrió un error en el servidor. Intente de nuevo más tarde.',
};

const MENSAJE_CONEXION =
  'No se pudo conectar con el servidor. Revise su conexión e intente de nuevo.';

function tipoPorEstado(status: number): TipoError {
  if (status === 400 || status === 422) return 'validacion';
  if (status === 401 || status === 403) return 'acceso';
  if (status === 404) return 'no_encontrado';
  if (status === 409) return 'conflicto';
  // Proxy o pasarela sin backend disponible.
  if (status === 502 || status === 503 || status === 504) return 'conexion';
  return 'servidor';
}

const esCodigoConocido = (codigo: string): codigo is CodigoErrorEvaluacion => codigo in MENSAJES;

/** Convierte cualquier fallo de una llamada en un error listo para mostrar. */
export function aErrorMostrable(error: unknown): ErrorMostrable {
  if (error instanceof ApiError) {
    const tipo = tipoPorEstado(error.status);
    const codigo = error.error?.codigo ?? `HTTP_${error.status}`;
    const mensaje =
      tipo === 'conexion'
        ? MENSAJE_CONEXION
        : esCodigoConocido(codigo)
          ? MENSAJES[codigo]
          : (error.error?.mensaje ?? MENSAJES.ERROR_INTERNO);
    return { tipo, codigo, mensaje, detalles: error.error?.detalles ?? [] };
  }
  // fetch lanza TypeError cuando no hay red o el servidor no responde.
  if (error instanceof TypeError) {
    return { tipo: 'conexion', codigo: 'SIN_CONEXION', mensaje: MENSAJE_CONEXION, detalles: [] };
  }
  return {
    tipo: 'servidor',
    codigo: 'ERROR_DESCONOCIDO',
    mensaje: MENSAJES.ERROR_INTERNO,
    detalles: [],
  };
}

/** Título corto por categoría, para el encabezado del aviso. */
export const TITULOS_ERROR: Record<TipoError, string> = {
  validacion: 'Datos no válidos',
  acceso: 'Acceso denegado',
  no_encontrado: 'No encontrado',
  conflicto: 'No se puede continuar',
  conexion: 'Sin conexión',
  servidor: 'Error del servidor',
};

/** Nombre visible de los campos que el backend devuelve en `detalles`. */
const ETIQUETAS_CAMPO: Record<string, string> = {
  testId: 'Test',
  versionFormularioId: 'Versión del formulario',
  sujetoId: 'Evaluado',
  periodoId: 'Período',
  subpruebas: 'Subpruebas',
  'fluidez.letraInicial': 'Letra inicial de Fluidez Verbal',
};

export const etiquetaCampo = (campo: string): string => ETIQUETAS_CAMPO[campo] ?? campo;
