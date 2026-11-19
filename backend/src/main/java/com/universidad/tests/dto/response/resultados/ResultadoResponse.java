package com.universidad.tests.dto.response.resultados;

import java.util.List;

/**
 * Respuesta de E9 ({@code ResultadoResponse} en docs/api/openapi.yaml). Los campos nulos se envían
 * como {@code null}: el frontend no debe suponer valores.
 */
public record ResultadoResponse(
        Long evaluacionId,
        Long resultadoId,
        String estadoResultado,
        Long versionFormularioId,
        Long calificacionId,
        Integer revision,
        String versionReglas,
        Long idClave,
        Long idBaremo,
        List<FactorResponse> factores) {

    public record FactorResponse(
            String codigo,
            Integer puntuacionDirecta,
            String estadoCalculo,
            Integer percentil,
            Long idRango,
            String estadoNormativo,
            String motivo) {}
}
