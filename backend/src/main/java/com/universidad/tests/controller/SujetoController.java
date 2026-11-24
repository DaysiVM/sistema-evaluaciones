package com.universidad.tests.controller;

import com.universidad.tests.application.usecase.ConsultarSujetoUseCase;
import com.universidad.tests.dto.response.SujetoResponse;
import com.universidad.tests.mapper.EvaluacionMapper;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** E19 — Consultar un Sujeto. El alta (E18) queda fuera de GEN-02. */
@RestController
@RequestMapping("/api/v1/sujetos")
public class SujetoController {

    private final ConsultarSujetoUseCase consultarSujeto;
    private final EvaluacionMapper mapper;

    public SujetoController(ConsultarSujetoUseCase consultarSujeto, EvaluacionMapper mapper) {
        this.consultarSujeto = consultarSujeto;
        this.mapper = mapper;
    }

    @GetMapping("/{sujetoId}")
    public SujetoResponse consultar(@PathVariable @Positive Long sujetoId) {
        return mapper.aResponse(consultarSujeto.consultar(sujetoId));
    }
}
