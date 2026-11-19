package com.universidad.tests.infrastructure.persistence.resultados;

import com.universidad.tests.domain.model.resultados.ContenedorResultado;
import com.universidad.tests.domain.model.resultados.EvaluacionConsultada;
import com.universidad.tests.domain.port.resultados.EvaluacionConsultaPort;
import com.universidad.tests.domain.port.resultados.ResultadoConsultaPort;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Lectura provisional de evaluaciones y resultados para E9.
 *
 * <p>Las tablas de Evaluacion (GEN-02-BE, PR #55) y de Resultado y sus revisiones (GEN-04-BE, PR
 * #64) todavía no están en {@code main}. Para no duplicar ese esquema, este adaptador no inventa
 * datos: falla de forma explícita y E9 responde 500 {@code ERROR_INTERNO}. Cuando esas tablas
 * entren, se reemplaza por un adaptador JDBC/JPA activado con el perfil {@code
 * persistencia-resultados}.
 */
@Component
@Profile("!persistencia-resultados")
public class ConsultaResultadosPendienteAdapter
        implements EvaluacionConsultaPort, ResultadoConsultaPort {

    private static final String PENDIENTE =
            "Persistencia de evaluaciones y resultados pendiente (GEN-02-BE #55 y GEN-04-BE #64)";

    @Override
    public Optional<EvaluacionConsultada> buscarPorId(Long evaluacionId) {
        throw new IllegalStateException(PENDIENTE);
    }

    @Override
    public Optional<ContenedorResultado> buscarPorEvaluacion(Long evaluacionId) {
        throw new IllegalStateException(PENDIENTE);
    }
}
