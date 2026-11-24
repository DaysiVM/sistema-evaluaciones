package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Evaluacion;
import java.util.Optional;

public interface EvaluacionRepository {

    Optional<Evaluacion> buscarPorId(Long idEvaluacion);

    boolean existeEnCurso(Long idSujeto, Long idTest, Long idPeriodo);

    /**
     * Guarda una evaluación nueva con sus subpruebas. Si otra solicitud creó antes una evaluación
     * abierta para el mismo sujeto, test y período, lanza EVALUACION_EN_CURSO.
     */
    Evaluacion guardarNueva(Evaluacion evaluacion);
}
