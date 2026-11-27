package com.universidad.tests.application.service;

import com.universidad.tests.domain.exception.EvaluacionNoEncontradaException;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.port.AutorizacionEvaluacionPort;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.dto.response.EvaluacionResponse;
import java.time.Clock;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// E4: estado y progreso de una evaluacion; el tiempo restante lo calcula el servidor
@Service
public class ConsultaEvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final Clock reloj;
    private final AutorizacionEvaluacionPort autorizacion;
    private final CierreSubpruebasService cierre;

    @Autowired
    public ConsultaEvaluacionService(
            EvaluacionRepository evaluacionRepository,
            AutorizacionEvaluacionPort autorizacion,
            CierreSubpruebasService cierre) {
        this(evaluacionRepository, autorizacion, cierre, Clock.systemUTC());
    }

    // Reloj inyectable para probar el tiempo restante
    public ConsultaEvaluacionService(
            EvaluacionRepository evaluacionRepository,
            AutorizacionEvaluacionPort autorizacion,
            CierreSubpruebasService cierre,
            Clock reloj) {
        this.autorizacion = autorizacion;
        this.cierre = cierre;
        this.evaluacionRepository = evaluacionRepository;
        this.reloj = reloj;
    }

    public EvaluacionResponse consultar(Long idEvaluacion) {
        Evaluacion evaluacion =
                evaluacionRepository
                        .buscarPorId(idEvaluacion)
                        .orElseThrow(() -> new EvaluacionNoEncontradaException(idEvaluacion));
        autorizacion.exigirPermisoConsultarEvaluacion(
                evaluacion.idEvaluacion(), evaluacion.idSujeto());
        cierre.cerrarVencidas(idEvaluacion);
        evaluacion =
                evaluacionRepository
                        .buscarPorId(idEvaluacion)
                        .orElseThrow(() -> new EvaluacionNoEncontradaException(idEvaluacion));
        Instant ahora = reloj.instant();

        return EvaluacionResponseMapper.aRespuesta(evaluacion, ahora);
    }
}
