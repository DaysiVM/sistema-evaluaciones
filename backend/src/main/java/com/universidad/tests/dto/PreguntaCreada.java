package com.universidad.tests.dto;

public record PreguntaCreada(
        Long id,
        Integer numeroVisible,
        Integer ordenPresentacion,
        String enunciado,
        String recursoRef) {}
