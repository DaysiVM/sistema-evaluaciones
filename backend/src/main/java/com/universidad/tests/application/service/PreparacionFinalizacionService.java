package com.universidad.tests.application.service;

import com.universidad.tests.domain.exception.EvaluacionNoEncontradaException;
import com.universidad.tests.domain.exception.SubpruebasPendientesException;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.EstadoSubprueba;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import org.springframework.stereotype.Service;

/** Validación previa de E8; no finaliza la evaluación ni crea resultados o eventos de auditoría. */
@Service
public class PreparacionFinalizacionService {
    private final CierreSubpruebasService cierre;
    private final EvaluacionRepository evaluaciones;

    public PreparacionFinalizacionService(
            CierreSubpruebasService cierre, EvaluacionRepository evaluaciones) {
        this.cierre = cierre;
        this.evaluaciones = evaluaciones;
    }

    public Evaluacion preparar(Long idEvaluacion) {
        cierre.cerrarVencidas(idEvaluacion);
        Evaluacion evaluacion =
                evaluaciones
                        .buscarPorId(idEvaluacion)
                        .orElseThrow(() -> new EvaluacionNoEncontradaException(idEvaluacion));
        if (evaluacion.estado() == EstadoEvaluacion.FINALIZADA) return evaluacion;
        if (evaluacion.subpruebas().stream().anyMatch(s -> s.estado() != EstadoSubprueba.CERRADA)) {
            throw new SubpruebasPendientesException();
        }
        return evaluacion;
    }
}
