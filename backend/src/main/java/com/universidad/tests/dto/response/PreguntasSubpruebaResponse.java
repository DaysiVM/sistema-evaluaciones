package com.universidad.tests.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public record PreguntasSubpruebaResponse(
        String codigo,
        long segundosRestantes,
        List<Pregunta> preguntas,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<Palabra> palabras) {
    public record Pregunta(
            Long id,
            int numero,
            String enunciado,
            String recursoUrl,
            List<Opcion> opciones,
            String respuestaActual) {}

    public record Opcion(String letra, String texto, String recursoUrl) {}

    public record Palabra(int ordenEntrada, String texto) {}
}
