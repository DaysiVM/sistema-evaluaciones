package com.universidad.tests.domain.exception;

import java.util.List;

/** Errores de GEN-02 con los códigos de DOC-05 (E2, E3, E4, E19 y E21). */
public final class ErroresEvaluacion {

    private ErroresEvaluacion() {}

    public static ErrorNegocioException solicitudInvalida(String campo, String mensaje) {
        return new ErrorNegocioException(
                TipoErrorNegocio.SOLICITUD_INVALIDA,
                "SOLICITUD_INVALIDA",
                "La solicitud tiene datos inválidos.",
                List.of(new ErrorNegocioException.DetalleError(campo, mensaje)));
    }

    public static ErrorNegocioException sinPermiso() {
        return new ErrorNegocioException(
                TipoErrorNegocio.SIN_PERMISO,
                "SIN_PERMISO",
                "No tiene permiso para realizar esta acción.");
    }

    public static ErrorNegocioException testNoEncontrado(Long idTest) {
        return new ErrorNegocioException(
                TipoErrorNegocio.NO_ENCONTRADO,
                "TEST_NO_ENCONTRADO",
                "No existe un test activo con ID " + idTest + ".");
    }

    public static ErrorNegocioException sujetoNoEncontrado(Long idSujeto) {
        return new ErrorNegocioException(
                TipoErrorNegocio.NO_ENCONTRADO,
                "SUJETO_NO_ENCONTRADO",
                "No existe el evaluado con ID " + idSujeto + ".");
    }

    public static ErrorNegocioException periodoNoEncontrado(Long idPeriodo) {
        return new ErrorNegocioException(
                TipoErrorNegocio.NO_ENCONTRADO,
                "PERIODO_NO_ENCONTRADO",
                "No existe el período con ID " + idPeriodo + ".");
    }

    public static ErrorNegocioException evaluacionNoEncontrada(Long idEvaluacion) {
        return new ErrorNegocioException(
                TipoErrorNegocio.NO_ENCONTRADO,
                "EVALUACION_NO_ENCONTRADA",
                "No existe la evaluación con ID " + idEvaluacion + ".");
    }

    public static ErrorNegocioException versionNoPublicada(Long idVersion) {
        return new ErrorNegocioException(
                TipoErrorNegocio.CONFLICTO,
                "VERSION_NO_PUBLICADA",
                "La versión " + idVersion + " del formulario no está publicada.");
    }

    public static ErrorNegocioException testSinVersionPublicada(Long idTest) {
        return new ErrorNegocioException(
                TipoErrorNegocio.CONFLICTO,
                "VERSION_NO_PUBLICADA",
                "El test " + idTest + " no tiene una versión publicada.");
    }

    public static ErrorNegocioException versionIncompatible(String mensaje) {
        return new ErrorNegocioException(
                TipoErrorNegocio.REGLA_DE_NEGOCIO, "VERSION_INCOMPATIBLE", mensaje);
    }

    public static ErrorNegocioException subpruebaNoIncluida(List<String> codigos) {
        return new ErrorNegocioException(
                TipoErrorNegocio.REGLA_DE_NEGOCIO,
                "SUBPRUEBA_NO_INCLUIDA",
                "Las subpruebas "
                        + String.join(", ", codigos)
                        + " no pertenecen a la versión del formulario.");
    }

    public static ErrorNegocioException periodoNoDisponible(Long idPeriodo) {
        return new ErrorNegocioException(
                TipoErrorNegocio.REGLA_DE_NEGOCIO,
                "PERIODO_NO_DISPONIBLE",
                "El período " + idPeriodo + " no admite nuevas evaluaciones.");
    }

    public static ErrorNegocioException evaluacionEnCurso() {
        return new ErrorNegocioException(
                TipoErrorNegocio.CONFLICTO,
                "EVALUACION_EN_CURSO",
                "El evaluado ya tiene una evaluación abierta de este test en este período.");
    }

    public static ErrorNegocioException letraNoAsignada() {
        return new ErrorNegocioException(
                TipoErrorNegocio.CONFLICTO,
                "LETRA_NO_ASIGNADA",
                "La evaluación incluye Fluidez Verbal y no tiene letra inicial asignada.");
    }
}
