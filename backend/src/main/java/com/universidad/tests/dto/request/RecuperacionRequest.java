package com.universidad.tests.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Entrada E16; el flujo requiere un repositorio de recuperacion persistente. */
public record RecuperacionRequest(
        @NotBlank @Email @JsonDeserialize(using = TextoSolicitudDeserializer.class) String correo) {

    @Override
    public String toString() {
        return "RecuperacionRequest[datos protegidos]";
    }
}
