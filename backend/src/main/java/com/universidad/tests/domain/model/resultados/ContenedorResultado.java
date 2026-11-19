package com.universidad.tests.domain.model.resultados;

import java.util.Objects;

/**
 * Contenedor común de resultados de una evaluación (Resultado, ENT-COM-10).
 *
 * <p>{@code revisionVigente} es la revisión referenciada por {@code referenciaRevisionVigente}. Es
 * nula mientras no haya una revisión publicada. Nunca se reemplaza por la revisión de número mayor
 * (CU-BFA-P09).
 */
public record ContenedorResultado(
        Long resultadoId,
        Long evaluacionId,
        String estadoResultado,
        RevisionResultado revisionVigente) {

    public static final String CALCULANDO = "CALCULANDO";
    public static final String PENDIENTE_VALIDACION = "PENDIENTE_VALIDACION";
    public static final String DISPONIBLE = "DISPONIBLE";

    public ContenedorResultado {
        Objects.requireNonNull(resultadoId, "resultadoId es obligatorio");
        Objects.requireNonNull(evaluacionId, "evaluacionId es obligatorio");
        Objects.requireNonNull(estadoResultado, "estadoResultado es obligatorio");
    }

    public boolean tieneRevisionVigente() {
        return revisionVigente != null;
    }
}
