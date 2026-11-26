package com.universidad.tests.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Entrada E14; no normaliza ni incorpora reglas de fortaleza de contrasenas. */
public record InicioSesionRequest(
        @NotBlank @Email @JsonDeserialize(using = TextoSolicitudDeserializer.class) String correo,
        @NotNull
                @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                @JsonDeserialize(using = TextoSolicitudDeserializer.class)
                String contrasena) {

    @Override
    public String toString() {
        return "InicioSesionRequest[datos protegidos]";
    }
}
