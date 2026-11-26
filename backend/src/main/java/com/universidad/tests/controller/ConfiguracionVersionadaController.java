package com.universidad.tests.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.tests.application.service.ConfiguracionVersionadaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfiguracionVersionadaController {
    private final ConfiguracionVersionadaService servicio;
    private final ObjectMapper mapper;

    public ConfiguracionVersionadaController(
            ConfiguracionVersionadaService servicio, ObjectMapper mapper) {
        this.servicio = servicio;
        this.mapper = mapper;
    }

    @PutMapping("/api/v1/versiones/{versionFormularioId}/configuraciones/{codigo}")
    public ConfiguracionRequest guardar(
            @PathVariable Long versionFormularioId,
            @PathVariable String codigo,
            @Valid @RequestBody ConfiguracionRequest solicitud)
            throws JsonProcessingException {
        List<EjemploPractica> ejemplos =
                solicitud.ejemplos() == null ? List.of() : solicitud.ejemplos();
        if (solicitud.tienePractica() && ejemplos.isEmpty()) {
            throw new IllegalArgumentException("La practica necesita al menos un ejemplo");
        }
        if (!solicitud.tienePractica() && !ejemplos.isEmpty()) {
            throw new IllegalArgumentException("Los ejemplos requieren tienePractica=true");
        }
        String practicaJson =
                solicitud.tienePractica() ? mapper.writeValueAsString(ejemplos) : null;
        servicio.guardar(
                versionFormularioId,
                codigo,
                solicitud.orden(),
                solicitud.tiempoLimiteSegundos(),
                solicitud.tipoRespuesta(),
                solicitud.opciones(),
                solicitud.maximoEntradas(),
                solicitud.instrucciones(),
                practicaJson);
        return solicitud;
    }

    public record ConfiguracionRequest(
            @NotNull @Positive Integer orden,
            @Positive Integer tiempoLimiteSegundos,
            @NotBlank String tipoRespuesta,
            @Positive Integer opciones,
            @Positive Integer maximoEntradas,
            boolean tienePractica,
            @NotBlank String instrucciones,
            List<@NotNull @Valid EjemploPractica> ejemplos) {}

    public record EjemploPractica(
            @NotNull @Positive Integer numero,
            @NotBlank String enunciado,
            List<@NotNull @Valid OpcionEjemplo> opciones,
            String retroalimentacion) {}

    public record OpcionEjemplo(@NotBlank String letra, @NotBlank String texto) {}
}
