package com.universidad.tests.domain.model;

import java.time.Instant;

// Entrega durable de una subprueba cerrada al evaluador; procesadaEn nulo = pendiente
public record SolicitudCalculoSubprueba(
        Long idEvaluacionSubprueba,
        Long idEvaluacion,
        String codigoTest,
        String codigoSubprueba,
        Instant fechaCierre,
        Instant procesadaEn) {}
