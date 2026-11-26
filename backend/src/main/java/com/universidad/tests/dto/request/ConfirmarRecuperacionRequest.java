package com.universidad.tests.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;

/** Entrada E17; no incorpora parametros de politica que el contrato no define. */
public record ConfirmarRecuperacionRequest(
        @NotNull
                @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                @JsonDeserialize(using = TextoSolicitudDeserializer.class)
                String token,
        @NotNull
                @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                @JsonDeserialize(using = TextoSolicitudDeserializer.class)
                String nuevaContrasena) {

    @Override
    public String toString() {
        return "ConfirmarRecuperacionRequest[datos protegidos]";
    }
}
