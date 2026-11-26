package com.universidad.tests.dto.response;

import java.time.LocalDate;

/** E21 (misma forma que E20). */
public record PeriodoResponse(
        Long id,
        String codigo,
        String denominacion,
        LocalDate inicio,
        LocalDate fin,
        boolean disponible) {}
