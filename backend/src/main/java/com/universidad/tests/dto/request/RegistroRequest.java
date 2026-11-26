package com.universidad.tests.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Entrada E13 conforme a RegistroRequest del contrato aprobado. */
public record RegistroRequest(
        @NotBlank @Email @JsonDeserialize(using = TextoSolicitudDeserializer.class) String correo,
        @NotNull
                @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                @JsonDeserialize(using = TextoSolicitudDeserializer.class)
                String contrasena,
        @NotNull @Size(max = 120) @JsonDeserialize(using = TextoSolicitudDeserializer.class)
                String nombres,
        @NotNull @Size(max = 120) @JsonDeserialize(using = TextoSolicitudDeserializer.class)
                String apellidos,
        @NotNull @JsonDeserialize(using = TextoSolicitudDeserializer.class) String rol) {

    @Override
    public String toString() {
        return "RegistroRequest[datos protegidos]";
    }
}
