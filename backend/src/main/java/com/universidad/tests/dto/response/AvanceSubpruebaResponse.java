package com.universidad.tests.dto.response;

public record AvanceSubpruebaResponse(
        String codigo, int registradas, long totalRespondidas, long segundosRestantes) {}
