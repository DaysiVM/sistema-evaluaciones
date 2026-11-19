package com.universidad.tests.infrastructure.security.resultados;

import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException;
import com.universidad.tests.domain.model.resultados.EvaluacionConsultada;
import com.universidad.tests.domain.port.resultados.AutorizacionResultadoPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Autorización provisional de E9: niega toda consulta mientras no existan la sesión (E14) y los
 * permisos de DOC-04. Sigue el mismo criterio que {@code AutorizacionContenidoDenegadaAdapter}: es
 * más seguro negar que exponer resultados.
 *
 * <p>Con el perfil {@code seguridad-integrada} se debe registrar el adaptador real.
 */
@Component
@Profile("!seguridad-integrada")
public class AutorizacionResultadoDenegadaAdapter implements AutorizacionResultadoPort {

    @Override
    public void exigirPermisoConsultarResultados() {
        throw ConsultaResultadoException.sinPermiso();
    }

    @Override
    public void exigirAlcanceSobre(EvaluacionConsultada evaluacion) {
        throw ConsultaResultadoException.sinPermiso();
    }
}
