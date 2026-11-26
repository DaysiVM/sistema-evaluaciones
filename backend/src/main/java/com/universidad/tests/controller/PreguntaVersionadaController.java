package com.universidad.tests.controller;

import com.universidad.tests.application.service.AgregarPreguntaVersionadaService;
import com.universidad.tests.dto.PreguntaCreada;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/versiones/{versionFormularioId}/configuraciones/{codigo}/preguntas")
public class PreguntaVersionadaController {

    private final AgregarPreguntaVersionadaService servicio;

    public PreguntaVersionadaController(AgregarPreguntaVersionadaService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<PreguntaCreada> agregar(
            @PathVariable Long versionFormularioId,
            @PathVariable String codigo,
            @Valid @RequestBody PreguntaRequest solicitud) {
        PreguntaCreada creada =
                servicio.agregar(
                        versionFormularioId,
                        codigo,
                        solicitud.numeroVisible().toString(),
                        solicitud.ordenPresentacion(),
                        solicitud.enunciado(),
                        solicitud.recursoRef());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(
                        URI.create(
                                "/api/v1/versiones/"
                                        + versionFormularioId
                                        + "/preguntas/"
                                        + creada.id()))
                .body(creada);
    }

    public record PreguntaRequest(
            @NotNull @Positive Integer numeroVisible,
            @NotNull @Positive Integer ordenPresentacion,
            @NotBlank String enunciado,
            String recursoRef) {}
}
