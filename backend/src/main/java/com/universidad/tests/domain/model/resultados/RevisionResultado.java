package com.universidad.tests.domain.model.resultados;

import java.util.List;
import java.util.Objects;

/**
 * Revisión vigente de un resultado (CalificacionBFA, ENT-BFA-08, en BFA).
 *
 * <p>Solo identifica los insumos usados (reglas, clave y baremo); nunca contiene el contenido de la
 * clave. Los factores conservan el orden en que los guarda el evaluador.
 */
public record RevisionResultado(
        Long calificacionId,
        Integer numero,
        String versionReglas,
        Long idClave,
        Long idBaremo,
        List<FactorResultado> factores) {

    public RevisionResultado {
        Objects.requireNonNull(calificacionId, "calificacionId es obligatorio");
        Objects.requireNonNull(numero, "numero de revisión es obligatorio");
        factores = factores == null ? List.of() : List.copyOf(factores);
    }
}
