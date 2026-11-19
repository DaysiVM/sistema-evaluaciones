package com.universidad.tests.dto.response.resultados;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Formato común de error (Documento general §5.2.2, {@code ErrorResponse} en openapi.yaml). {@code
 * detalles} solo aparece en errores de validación.
 */
public record ErrorResultadoResponse(
        String timestamp,
        int status,
        String codigo,
        String mensaje,
        String ruta,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<Detalle> detalles) {

    public record Detalle(String campo, String mensaje) {}
}
