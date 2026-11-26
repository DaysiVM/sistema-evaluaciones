package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.RespuestaEvaluacion;
import java.util.List;

public interface RespuestaEvaluacionRepository {
    List<RespuestaEvaluacion> listar(Long evaluacionId, String codigo);

    long contar(Long evaluacionId, String codigo);

    // El llamador debe mantener el bloqueo de la evaluación durante toda la escritura.
    void registrar(RespuestaEvaluacion respuesta);
}
