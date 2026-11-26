package com.universidad.tests.controller;

import com.universidad.tests.application.usecase.RegistrarCuentaService;
import com.universidad.tests.dto.request.RegistroRequest;
import com.universidad.tests.dto.response.CuentaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth/registro", produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistroAuthController {

    private final RegistrarCuentaService registro;

    public RegistroAuthController(RegistrarCuentaService registro) {
        this.registro = registro;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponse registrar(@Valid @RequestBody RegistroRequest solicitud) {
        return registro.registrar(solicitud);
    }
}
