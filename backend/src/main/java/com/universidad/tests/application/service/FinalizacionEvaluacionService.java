package com.universidad.tests.application.service;

import com.universidad.tests.domain.exception.*;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.port.*;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.dto.response.EvaluacionResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cierre transaccional/idempotente. La consolidacion con insumos aprobados sigue en BFA-BE-38. */
@Service
public class FinalizacionEvaluacionService {
    private final AutorizacionService auth;
    private final AlcanceSujetoPort alcance;
    private final EvaluacionRepository evaluaciones;
    private final PreparacionFinalizacionService preparacion;
    private final FinalizacionEvaluacionPort cierre;
    private final RegistroAuditoria auditoria;

    public FinalizacionEvaluacionService(
            AutorizacionService auth,
            AlcanceSujetoPort alcance,
            EvaluacionRepository evaluaciones,
            PreparacionFinalizacionService preparacion,
            FinalizacionEvaluacionPort cierre,
            RegistroAuditoria auditoria) {
        this.auth = auth;
        this.alcance = alcance;
        this.evaluaciones = evaluaciones;
        this.preparacion = preparacion;
        this.cierre = cierre;
        this.auditoria = auditoria;
    }

    @Transactional
    public EvaluacionResponse finalizar(Long id) {
        var sesion = auth.exigirPermiso("EVALUACION_FINALIZAR");
        var original =
                evaluaciones
                        .buscarPorIdParaActualizar(id)
                        .orElseThrow(() -> new EvaluacionNoEncontradaException(id));
        if (!"SUJETO".equals(sesion.rolActivo())
                || !alcance.estaAsignado(sesion.idCuenta(), original.idSujeto()))
            throw new SinPermisoException();
        preparacion.preparar(id);
        var resultado = cierre.finalizar(id, Instant.now().truncatedTo(ChronoUnit.MICROS));
        if (original.estado() != EstadoEvaluacion.FINALIZADA)
            auditoria.registrar(sesion.idCuenta(), "FINALIZAR_EVALUACION", "EVALUACION", id);
        var e =
                evaluaciones
                        .buscarPorId(id)
                        .orElseThrow(() -> new EvaluacionNoEncontradaException(id));
        return new EvaluacionResponse(
                e.idEvaluacion(),
                e.idTest(),
                e.idSujeto(),
                e.idPeriodo(),
                e.idVersionFormulario(),
                e.estado().name(),
                e.fechaInicio(),
                e.fechaFin(),
                null,
                e.subpruebas().stream()
                        .map(
                                s ->
                                        new EvaluacionResponse.SubpruebaEstado(
                                                s.codigo(), s.estado().name(), s.fechaCierre()))
                        .toList(),
                resultado.resultadoId(),
                resultado.estadoResultado());
    }
}
