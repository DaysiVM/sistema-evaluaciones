package com.universidad.tests.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

public interface CierreSubpruebasRepository {
    List<Long> buscarEvaluacionesVencidas(Instant ahora, int limite);

    void cerrarVencidas(Long idEvaluacion, Supplier<Instant> ahora);
}
