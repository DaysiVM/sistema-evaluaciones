package com.universidad.tests.mapper;

import com.universidad.tests.application.usecase.IniciarEvaluacionComando;
import com.universidad.tests.application.usecase.vista.EvaluacionVista;
import com.universidad.tests.application.usecase.vista.PeriodoVista;
import com.universidad.tests.application.usecase.vista.SujetoVista;
import com.universidad.tests.dto.request.IniciarEvaluacionRequest;
import com.universidad.tests.dto.response.EvaluacionResponse;
import com.universidad.tests.dto.response.PeriodoResponse;
import com.universidad.tests.dto.response.SujetoResponse;
import org.springframework.stereotype.Component;

/** Convierte entre DTOs de la API y los datos de los casos de uso de GEN-02. */
@Component
public class EvaluacionMapper {

    public IniciarEvaluacionComando aComando(IniciarEvaluacionRequest request) {
        return new IniciarEvaluacionComando(
                request.testId(),
                request.versionFormularioId(),
                request.sujetoId(),
                request.periodoId(),
                request.subpruebas(),
                request.fluidez() == null ? null : request.fluidez().letraInicial());
    }

    public EvaluacionResponse aResponse(EvaluacionVista vista) {
        EvaluacionResponse.SubpruebaActivaResponse activa =
                vista.subpruebaActiva() == null
                        ? null
                        : new EvaluacionResponse.SubpruebaActivaResponse(
                                vista.subpruebaActiva().codigo(),
                                vista.subpruebaActiva().finLimite(),
                                vista.subpruebaActiva().segundosRestantes());
        return new EvaluacionResponse(
                vista.id(),
                vista.testId(),
                vista.versionFormularioId(),
                vista.sujetoId(),
                vista.periodoId(),
                vista.estado(),
                vista.fechaInicio(),
                vista.fechaFin(),
                activa,
                vista.subpruebas().stream()
                        .map(
                                s ->
                                        new EvaluacionResponse.SubpruebaEstadoResponse(
                                                s.codigo(), s.estado()))
                        .toList());
    }

    public SujetoResponse aResponse(SujetoVista vista) {
        return new SujetoResponse(
                vista.id(), vista.identificadorInstitucional(), vista.nombres(), vista.apellidos());
    }

    public PeriodoResponse aResponse(PeriodoVista vista) {
        return new PeriodoResponse(
                vista.id(),
                vista.codigo(),
                vista.denominacion(),
                vista.inicio(),
                vista.fin(),
                vista.disponible());
    }
}
