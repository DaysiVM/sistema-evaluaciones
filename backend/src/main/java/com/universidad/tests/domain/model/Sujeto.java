package com.universidad.tests.domain.model;

import java.time.LocalDate;

/** Persona evaluada común a todos los instrumentos (ENT-COM-03). */
public record Sujeto(
        Long idSujeto,
        String codigoInstitucional,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento) {

    public Sujeto {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException("Los nombres del sujeto son obligatorios");
        }
        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException("Los apellidos del sujeto son obligatorios");
        }
    }
}
