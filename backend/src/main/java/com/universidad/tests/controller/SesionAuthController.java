package com.universidad.tests.controller;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.usecase.AbrirSesionService;
import com.universidad.tests.domain.exception.NoAutenticadoException;
import com.universidad.tests.dto.request.InicioSesionRequest;
import com.universidad.tests.dto.response.SesionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth/sesion", produces = MediaType.APPLICATION_JSON_VALUE)
public class SesionAuthController {

    private final AutenticacionService autenticacion;
    private final AbrirSesionService sesiones;

    public SesionAuthController(AutenticacionService autenticacion, AbrirSesionService sesiones) {
        this.autenticacion = autenticacion;
        this.sesiones = sesiones;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public SesionResponse abrir(@Valid @RequestBody InicioSesionRequest request) {
        return sesiones.abrir(request);
    }

    @DeleteMapping
    public ResponseEntity<Void> cerrar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String cabecera) {
        if (cabecera == null || !cabecera.regionMatches(true, 0, "Bearer ", 0, 7)) {
            throw new NoAutenticadoException();
        }
        String token = cabecera.substring(7).trim();
        if (token.isEmpty()) {
            throw new NoAutenticadoException();
        }
        autenticacion.cerrarSesion(token);
        return ResponseEntity.noContent().build();
    }
}
