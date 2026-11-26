package com.universidad.tests.application.evaluator.bfa;

import java.util.Objects;

/**
 * Respuesta registrada en una pregunta de selección de un bloque BFA.
 *
 * @param idPregunta pregunta respondida
 * @param idOpcion opción elegida; {@code null} si la pregunta quedó sin contestar
 */
public record RespuestaSeleccionBfa(Long idPregunta, Long idOpcion) {

    public RespuestaSeleccionBfa {
        Objects.requireNonNull(idPregunta, "idPregunta es obligatorio");
    }
}
