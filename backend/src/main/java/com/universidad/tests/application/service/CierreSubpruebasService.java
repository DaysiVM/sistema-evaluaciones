package com.universidad.tests.application.service;

import com.universidad.tests.domain.repository.CierreSubpruebasRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CierreSubpruebasService {
    private static final Logger LOG = LoggerFactory.getLogger(CierreSubpruebasService.class);
    private final CierreSubpruebasRepository repositorio;
    private final Clock reloj;

    @Autowired
    public CierreSubpruebasService(CierreSubpruebasRepository repositorio) {
        this(repositorio, Clock.systemUTC());
    }

    public CierreSubpruebasService(CierreSubpruebasRepository repositorio, Clock reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    public void cerrarVencidas(Long idEvaluacion) {
        repositorio.cerrarVencidas(
                idEvaluacion, () -> reloj.instant().truncatedTo(ChronoUnit.MICROS));
    }

    public void procesarVencidas() {
        Instant ahora = reloj.instant().truncatedTo(ChronoUnit.MICROS);
        for (Long id : repositorio.buscarEvaluacionesVencidas(ahora, 100)) {
            try {
                cerrarVencidas(id);
            } catch (RuntimeException ex) {
                LOG.error("No se pudo cerrar una subprueba vencida de la evaluación {}", id, ex);
            }
        }
    }
}
