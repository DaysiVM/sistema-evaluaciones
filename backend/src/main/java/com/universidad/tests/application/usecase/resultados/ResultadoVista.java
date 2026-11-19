package com.universidad.tests.application.usecase.resultados;

import java.util.List;

/**
 * Lo que devuelve la consulta de resultados (E9), sin entidades de dominio. El controlador lo
 * convierte en {@code ResultadoResponse}.
 */
public record ResultadoVista(
        Long evaluacionId,
        Long resultadoId,
        String estadoResultado,
        Long versionFormularioId,
        Long calificacionId,
        Integer revision,
        String versionReglas,
        Long idClave,
        Long idBaremo,
        List<FactorVista> factores) {

    public ResultadoVista {
        factores = factores == null ? List.of() : List.copyOf(factores);
    }

    public record FactorVista(
            String codigo,
            Integer puntuacionDirecta,
            String estadoCalculo,
            Integer percentil,
            Long idRango,
            String estadoNormativo,
            String motivo) {}
}
