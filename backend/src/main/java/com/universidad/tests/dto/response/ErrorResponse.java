package com.universidad.tests.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

/** Formato común de error (Documento general §5.2.2); {@code detalles} solo en validación. */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String codigo,
        String mensaje,
        String ruta,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) List<DetalleCampo> detalles) {

    /** Error sin detalles por campo (lo usan GEN-01 y los errores de negocio). */
    public ErrorResponse(
            Instant timestamp, int status, String codigo, String mensaje, String ruta) {
        this(timestamp, status, codigo, mensaje, ruta, List.of());
    }

    public record DetalleCampo(String campo, String mensaje) {}
}
