package com.universidad.tests.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record RegistrarRespuestasRequest(List<Seleccion> respuestas, List<Palabra> palabras) {
    public record Seleccion(Long preguntaId, String opcion) {
        @JsonCreator
        static Seleccion desdeJson(
                @JsonProperty("preguntaId") JsonNode id, @JsonProperty("opcion") JsonNode opcion) {
            if (id == null || !id.isIntegralNumber() || !id.canConvertToLong()) {
                throw new IllegalArgumentException("preguntaId debe ser un entero de 64 bits");
            }
            return new Seleccion(id.longValue(), texto(opcion));
        }
    }

    public record Palabra(Integer ordenEntrada, String texto) {
        @JsonCreator
        static Palabra desdeJson(
                @JsonProperty("ordenEntrada") JsonNode orden,
                @JsonProperty("texto") JsonNode texto) {
            if (orden == null || !orden.isIntegralNumber() || !orden.canConvertToInt()) {
                throw new IllegalArgumentException("ordenEntrada debe ser un entero");
            }
            return new Palabra(orden.intValue(), RegistrarRespuestasRequest.texto(texto));
        }
    }

    private static String texto(JsonNode valor) {
        if (valor == null || !valor.isTextual()) {
            throw new IllegalArgumentException("Se requiere un valor de tipo texto");
        }
        return valor.textValue();
    }
}
