package com.universidad.tests.application.usecase.vista;

import java.time.LocalDate;

public record PeriodoVista(
        Long id,
        String codigo,
        String denominacion,
        LocalDate inicio,
        LocalDate fin,
        boolean disponible) {}
