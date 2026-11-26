package com.universidad.tests.controller;

import com.universidad.tests.application.service.VersionFormularioRestService;
import com.universidad.tests.application.service.VersionFormularioRestService.ResumenVersion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionFormularioController {
    private final VersionFormularioRestService servicio;

    public VersionFormularioController(VersionFormularioRestService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/api/v1/tests/{testId}/versiones")
    public ResponseEntity<ResumenVersion> crear(
            @PathVariable Long testId, @Valid @RequestBody CrearVersionRequest solicitud) {
        ResumenVersion version =
                servicio.crear(
                        testId,
                        solicitud.codigoVersion(),
                        solicitud.descripcion(),
                        solicitud.fuenteRef());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/versiones/" + version.id()))
                .body(version);
    }

    public record CrearVersionRequest(
            @NotBlank String codigoVersion, String descripcion, @NotBlank String fuenteRef) {}
}
