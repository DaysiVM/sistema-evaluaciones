package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.EstadoEvaluacion;
import java.util.List;

public interface ListadoEvaluacionesRepository {
    List<Long> buscarIds(
            Long cuentaRestringida,
            Long sujetoId,
            Long testId,
            Long periodoId,
            EstadoEvaluacion estado);
}
