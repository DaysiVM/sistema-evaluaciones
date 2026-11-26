package com.universidad.tests.controller;

import com.universidad.tests.application.service.RecuperacionCuentaService;
import com.universidad.tests.dto.request.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/recuperacion")
public class RecuperacionAuthController {
    private final RecuperacionCuentaService servicio;

    public RecuperacionAuthController(RecuperacionCuentaService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<Void> pedir(@Valid @RequestBody RecuperacionRequest request) {
        servicio.pedir(request.correo());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Void> confirmar(
            @Valid @RequestBody ConfirmarRecuperacionRequest request) {
        servicio.confirmar(request.token(), request.nuevaContrasena());
        return ResponseEntity.noContent().build();
    }
}
