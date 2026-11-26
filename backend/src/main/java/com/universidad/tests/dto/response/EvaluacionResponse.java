package com.universidad.tests.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

// E4 — Consultar estado y progreso (schema EvaluacionResponse del contrato)
// resultadoId y estadoResultado se incluyen solo en E8.
public record EvaluacionResponse(
        Long id,
        Long testId,
        Long sujetoId,
        Long periodoId,
        Long versionFormularioId,
        String estado,
        Instant fechaInicio,
        @JsonInclude(JsonInclude.Include.ALWAYS) Instant fechaFin,
        @JsonInclude(JsonInclude.Include.ALWAYS) SubpruebaActiva subpruebaActiva,
        List<SubpruebaEstado> subpruebas,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long resultadoId,
        @JsonInclude(JsonInclude.Include.NON_NULL) String estadoResultado) {
    public EvaluacionResponse(
            Long id,
            Long testId,
            Long sujetoId,
            Long periodoId,
            Long versionFormularioId,
            String estado,
            Instant fechaInicio,
            Instant fechaFin,
            SubpruebaActiva subpruebaActiva,
            List<SubpruebaEstado> subpruebas) {
        this(
                id,
                testId,
                sujetoId,
                periodoId,
                versionFormularioId,
                estado,
                fechaInicio,
                fechaFin,
                subpruebaActiva,
                subpruebas,
                null,
                null);
    }

    public record SubpruebaActiva(String codigo, Instant finLimite, long segundosRestantes) {}

    public record SubpruebaEstado(
            String codigo,
            String estado,
            @JsonInclude(JsonInclude.Include.ALWAYS) Instant cerradaEn) {}
}
