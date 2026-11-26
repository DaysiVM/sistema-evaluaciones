package com.universidad.tests.domain.port;

import java.time.Instant;

public interface FinalizacionEvaluacionPort {
    Cierre finalizar(Long evaluacion, Instant ahora);

    record Cierre(Long resultadoId, String estadoResultado) {}
}
