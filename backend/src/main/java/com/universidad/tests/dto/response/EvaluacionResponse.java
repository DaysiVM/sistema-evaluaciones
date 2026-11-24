package com.universidad.tests.dto.response;

import java.time.Instant;
import java.util.List;

/** E3 (201) y E4 (200). {@code versionFormularioId}: ajuste propuesto en DOC-05 §5.4.1. */
public record EvaluacionResponse(
        Long id,
        Long testId,
        Long versionFormularioId,
        Long sujetoId,
        Long periodoId,
        String estado,
        Instant fechaInicio,
        Instant fechaFin,
        SubpruebaActivaResponse subpruebaActiva,
        List<SubpruebaEstadoResponse> subpruebas) {

    public record SubpruebaActivaResponse(
            String codigo, Instant finLimite, Long segundosRestantes) {}

    public record SubpruebaEstadoResponse(String codigo, String estado) {}
}
