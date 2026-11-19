package com.universidad.tests.domain.port.resultados;

import com.universidad.tests.domain.model.resultados.EvaluacionConsultada;
import java.util.Optional;

/** Lectura de la evaluación para la consulta de resultados. No modifica nada. */
public interface EvaluacionConsultaPort {

    Optional<EvaluacionConsultada> buscarPorId(Long evaluacionId);
}
