package com.universidad.tests.controller;

import com.universidad.tests.application.usecase.IniciarEvaluacionUseCase;
import com.universidad.tests.dto.request.IniciarEvaluacionRequest;
import com.universidad.tests.dto.response.EvaluacionResponse;
import com.universidad.tests.mapper.EvaluacionMapper;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GEN-02-BE: E3 (iniciar una evaluación). E4 (recuperar) pertenece a GEN-03-BE (#41), según la
 * decisión del Scrum Master del 23/11/2026; por eso este controlador solo declara el POST.
 */
@RestController
@RequestMapping("/api/v1/evaluaciones")
public class InicioEvaluacionController {

    private final IniciarEvaluacionUseCase iniciarEvaluacion;
    private final EvaluacionMapper mapper;

    public InicioEvaluacionController(
            IniciarEvaluacionUseCase iniciarEvaluacion, EvaluacionMapper mapper) {
        this.iniciarEvaluacion = iniciarEvaluacion;
        this.mapper = mapper;
    }

    /** E3 — 201 con cabecera Location. */
    @PostMapping
    public ResponseEntity<EvaluacionResponse> iniciar(
            @Valid @RequestBody IniciarEvaluacionRequest request) {
        EvaluacionResponse respuesta =
                mapper.aResponse(iniciarEvaluacion.iniciar(mapper.aComando(request)));
        return ResponseEntity.created(URI.create("/api/v1/evaluaciones/" + respuesta.id()))
                .body(respuesta);
    }
}
