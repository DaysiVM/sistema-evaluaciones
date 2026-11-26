package com.universidad.tests.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** E18 — Solicitud para registrar a la persona evaluada (CU-GEN-20). */
public record SujetoRequest(
        @NotBlank(message = "El identificador institucional es obligatorio")
                @Size(
                        max = 80,
                        message = "El identificador institucional no debe exceder 80 caracteres")
                String identificadorInstitucional,
        @NotBlank(message = "Los nombres son obligatorios")
                @Size(max = 200, message = "Los nombres no deben exceder 200 caracteres")
                String nombres,
        @NotBlank(message = "Los apellidos son obligatorios")
                @Size(max = 200, message = "Los apellidos no deben exceder 200 caracteres")
                String apellidos,
        LocalDate fechaNacimiento) {}
