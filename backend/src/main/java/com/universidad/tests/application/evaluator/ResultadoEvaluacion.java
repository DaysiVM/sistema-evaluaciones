package com.universidad.tests.application.evaluator;

import java.util.Map;
import java.util.Objects;

/** Representa el resultado consolidado por un evaluador de test psicométrico. */
public record ResultadoEvaluacion(
        Long resultadoId,
        String estadoResultado,
        Map<String, FactorCalculado> factores,
        String detalle) {

    public static final String PENDIENTE_VALIDACION = "PENDIENTE_VALIDACION";
    public static final String CALCULANDO = "CALCULANDO";
    public static final String DISPONIBLE = "DISPONIBLE";

    public ResultadoEvaluacion {
        Objects.requireNonNull(estadoResultado, "estadoResultado es obligatorio");
    }

    public static ResultadoEvaluacion pendienteValidacion(
            Long resultadoId, Map<String, FactorCalculado> factores, String detalle) {
        return new ResultadoEvaluacion(resultadoId, PENDIENTE_VALIDACION, factores, detalle);
    }
}
