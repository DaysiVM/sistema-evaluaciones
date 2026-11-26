package com.universidad.tests.controller;

import com.universidad.tests.application.service.OpcionesVersionadasService;
import com.universidad.tests.application.service.OpcionesVersionadasService.DatosOpcion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestController
public class OpcionesVersionadasController {
    private final OpcionesVersionadasService servicio;

    public OpcionesVersionadasController(OpcionesVersionadasService servicio) {
        this.servicio = servicio;
    }

    @PutMapping("/api/v1/versiones/{versionFormularioId}/preguntas/{preguntaId}/opciones")
    public OpcionesRequest guardar(
            @PathVariable Long versionFormularioId,
            @PathVariable Long preguntaId,
            @Valid @RequestBody OpcionesRequest solicitud) {
        List<DatosOpcion> datos = new ArrayList<>();
        for (OpcionRequest opcion : solicitud.opciones()) {
            int orden = opcion.orden() == null ? datos.size() + 1 : opcion.orden();
            datos.add(new DatosOpcion(opcion.letra(), orden, opcion.texto(), opcion.recursoRef()));
        }
        return new OpcionesRequest(
                servicio.guardar(versionFormularioId, preguntaId, datos).stream()
                        .map(
                                o ->
                                        new OpcionRequest(
                                                o.letra(), o.orden(), o.texto(), o.recursoRef()))
                        .toList());
    }

    public record OpcionesRequest(@NotEmpty List<@NotNull @Valid OpcionRequest> opciones) {}

    public record OpcionRequest(
            @NotBlank String letra, @Positive Integer orden, String texto, String recursoRef) {}
}

@RestControllerAdvice(assignableTypes = OpcionesVersionadasController.class)
class ErroresOpcionesVersionadas extends VersionadoErrorHandler {}
