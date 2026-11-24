package com.universidad.tests.application.usecase.vista;

import com.universidad.tests.domain.model.Evaluacion;
import java.time.Instant;
import java.util.List;

/** Estado de una evaluación tal como lo devuelven E3 (201) y E4 (200). */
public record EvaluacionVista(
        Long id,
        Long testId,
        Long versionFormularioId,
        Long sujetoId,
        Long periodoId,
        String estado,
        Instant fechaInicio,
        Instant fechaFin,
        SubpruebaActivaVista subpruebaActiva,
        List<SubpruebaEstadoVista> subpruebas) {

    /** El tiempo restante se calcula con la hora del servidor; no modifica la evaluación. */
    public static EvaluacionVista desde(Evaluacion evaluacion, Instant ahora) {
        SubpruebaActivaVista activa =
                evaluacion
                        .subpruebaActiva()
                        .map(
                                s ->
                                        new SubpruebaActivaVista(
                                                s.getCodigo(),
                                                s.getFinLimite(),
                                                s.segundosRestantes(ahora)))
                        .orElse(null);

        List<SubpruebaEstadoVista> subpruebas =
                evaluacion.getSubpruebas().stream()
                        .map(s -> new SubpruebaEstadoVista(s.getCodigo(), s.getEstado().name()))
                        .toList();

        return new EvaluacionVista(
                evaluacion.getIdEvaluacion(),
                evaluacion.getIdTest(),
                evaluacion.getIdVersionFormulario(),
                evaluacion.getIdSujeto(),
                evaluacion.getIdPeriodo(),
                evaluacion.getEstado().name(),
                evaluacion.getIniciadaEn(),
                evaluacion.getFinalizadaEn(),
                activa,
                subpruebas);
    }
}
