package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import java.util.Optional;

public interface EvaluacionRepository {

    // Devuelve la evaluacion con sus subpruebas ordenadas por orden de aplicacion
    Optional<Evaluacion> buscarPorId(Long idEvaluacion);

    // Requiere una transacciÃ³n de escritura. Serializa cambios del motor por evaluaciÃ³n.
    Optional<Evaluacion> buscarPorIdParaActualizar(Long idEvaluacion);

    void iniciarSubprueba(EvaluacionSubprueba subprueba);

    boolean existeEnCurso(Long idSujeto, Long idTest, Long idPeriodo);

    // Inserta (id nulo) la evaluacion y sus subpruebas
    Evaluacion guardar(Evaluacion evaluacion);

    default Evaluacion guardarNueva(Evaluacion evaluacion) {
        return guardar(evaluacion);
    }
}
