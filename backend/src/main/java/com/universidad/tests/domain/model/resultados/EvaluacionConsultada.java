package com.universidad.tests.domain.model.resultados;

import java.util.Objects;

/**
 * Datos mínimos de una evaluación que necesita la consulta de resultados (ENT-COM-05).
 *
 * <p>{@code sujetoId} permite que la autorización compruebe el alcance sobre la persona evaluada.
 */
public record EvaluacionConsultada(
        Long evaluacionId, Long sujetoId, Long versionFormularioId, String estado) {

    public static final String FINALIZADA = "FINALIZADA";

    public EvaluacionConsultada {
        Objects.requireNonNull(evaluacionId, "evaluacionId es obligatorio");
        Objects.requireNonNull(estado, "estado es obligatorio");
    }

    public boolean estaFinalizada() {
        return FINALIZADA.equals(estado);
    }
}
