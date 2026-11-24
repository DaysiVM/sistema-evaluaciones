package com.universidad.tests.controller;

import com.universidad.tests.application.usecase.ListarPeriodosUseCase;
import com.universidad.tests.dto.response.PeriodoResponse;
import com.universidad.tests.mapper.EvaluacionMapper;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** E21 — Listar períodos. El alta (E20) queda fuera de GEN-02. */
@RestController
@RequestMapping("/api/v1/periodos")
public class PeriodoController {

    private final ListarPeriodosUseCase listarPeriodos;
    private final EvaluacionMapper mapper;

    public PeriodoController(ListarPeriodosUseCase listarPeriodos, EvaluacionMapper mapper) {
        this.listarPeriodos = listarPeriodos;
        this.mapper = mapper;
    }

    @GetMapping
    public List<PeriodoResponse> listar(
            @RequestParam(name = "disponible", defaultValue = "false") boolean disponible) {
        return listarPeriodos.listar(disponible).stream().map(mapper::aResponse).toList();
    }
}
