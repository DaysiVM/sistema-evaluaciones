package com.universidad.tests.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Cuerpo de E3 (Documento BFA §5.3). Si se omite {@code subpruebas}, la aplicación es completa. */
public record IniciarEvaluacionRequest(
        @Positive Long testId,
        @NotNull @Positive Long versionFormularioId,
        @NotNull @Positive Long sujetoId,
        @NotNull @Positive Long periodoId,
        @Size(min = 1, message = "si se envía, debe incluir al menos una subprueba")
                List<@NotBlank String> subpruebas,
        @Valid FluidezRequest fluidez) {

    /** Letra inicial de Fluidez Verbal (RF-FV-06). */
    public record FluidezRequest(
            @NotBlank
                    @Pattern(
                            regexp = "^[A-Za-zÑñ]$",
                            message = "debe ser una sola letra del alfabeto")
                    String letraInicial) {}
}
