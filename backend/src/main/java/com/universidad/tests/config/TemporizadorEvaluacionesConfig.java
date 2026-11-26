package com.universidad.tests.config;

import com.universidad.tests.application.service.CierreSubpruebasService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
        name = "motor.temporizador.habilitado",
        havingValue = "true",
        matchIfMissing = true)
public class TemporizadorEvaluacionesConfig {
    private final CierreSubpruebasService cierre;

    public TemporizadorEvaluacionesConfig(CierreSubpruebasService cierre) {
        this.cierre = cierre;
    }

    @Scheduled(fixedDelayString = "${motor.temporizador.intervalo-ms:1000}")
    public void cerrarVencidas() {
        cierre.procesarVencidas();
    }
}
