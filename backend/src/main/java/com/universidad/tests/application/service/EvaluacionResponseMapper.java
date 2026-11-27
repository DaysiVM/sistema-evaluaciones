package com.universidad.tests.application.service;

import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.dto.response.EvaluacionResponse;
import java.time.Instant;

final class EvaluacionResponseMapper {
    private EvaluacionResponseMapper() {}

    static EvaluacionResponse aRespuesta(Evaluacion evaluacion, Instant ahora) {
        return new EvaluacionResponse(
                evaluacion.idEvaluacion(),
                evaluacion.idTest(),
                evaluacion.idSujeto(),
                evaluacion.idPeriodo(),
                evaluacion.idVersionFormulario(),
                evaluacion.estado().name(),
                evaluacion.fechaInicio(),
                evaluacion.fechaFin(),
                evaluacion
                        .subpruebaActiva()
                        .map(
                                s ->
                                        new EvaluacionResponse.SubpruebaActiva(
                                                s.codigo(),
                                                s.fechaFinLimite(),
                                                s.segundosRestantes(ahora)))
                        .orElse(null),
                evaluacion.subpruebas().stream()
                        .map(
                                s ->
                                        new EvaluacionResponse.SubpruebaEstado(
                                                s.codigo(), s.estado().name(), s.fechaCierre()))
                        .toList());
    }
}
