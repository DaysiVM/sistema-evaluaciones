package com.universidad.tests.domain.model;

import java.time.Instant;

// Respuesta a una pregunta, o palabra de Fluidez Verbal (idPregunta nulo, valorTexto obligatorio)
public record RespuestaEvaluacion(
        Long idRespuestaEvaluacion,
        Long idEvaluacion,
        String subpruebaCodigo,
        Long idPregunta,
        Long idOpcion,
        String valorTexto,
        int ordenEntrada,
        Instant fechaRegistro) {}
