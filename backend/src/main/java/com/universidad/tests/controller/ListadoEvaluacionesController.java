package com.universidad.tests.controller;

import com.universidad.tests.application.service.ListadoEvaluacionesService;
import com.universidad.tests.dto.request.EstadoListadoEvaluacion;
import com.universidad.tests.dto.response.EvaluacionResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/evaluaciones", produces = "application/json")
public class ListadoEvaluacionesController {
    private final ListadoEvaluacionesService servicio;

    public ListadoEvaluacionesController(ListadoEvaluacionesService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<EvaluacionResponse> listar(
            @RequestParam(required = false) Long sujetoId,
            @RequestParam(required = false) Long testId,
            @RequestParam(required = false) Long periodoId,
            @RequestParam(required = false) EstadoListadoEvaluacion estado) {
        return servicio.listar(sujetoId, testId, periodoId, estado);
    }
}
