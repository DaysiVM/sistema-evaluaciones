package com.universidad.tests.dto.response;

import java.time.Instant;

public record SubpruebaActivaResponse(
        String codigo,
        String estado,
        Instant inicio,
        Instant finLimite,
        long segundosRestantes,
        String letraInicial) {}
