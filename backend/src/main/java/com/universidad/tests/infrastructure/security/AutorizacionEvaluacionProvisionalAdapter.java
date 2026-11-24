package com.universidad.tests.infrastructure.security;

import com.universidad.tests.domain.port.AutorizacionEvaluacionPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementación provisional mientras no existe GEN-06 (sesión, roles y permisos): permite las
 * operaciones de GEN-02 para poder integrar y probar el flujo. Con el perfil {@code
 * seguridad-integrada} debe existir la implementación real, que lanza SIN_PERMISO (403). No usar en
 * producción con datos reales.
 */
@Component
@Profile("!seguridad-integrada")
public class AutorizacionEvaluacionProvisionalAdapter implements AutorizacionEvaluacionPort {

    @Override
    public void exigirPermisoIniciarEvaluacion(Long idSujeto) {
        // Pendiente de GEN-06.
    }

    @Override
    public void exigirPermisoConsultarEvaluacion(Long idEvaluacion, Long idSujeto) {
        // Pendiente de GEN-06.
    }

    @Override
    public void exigirPermisoLeerSujeto(Long idSujeto) {
        // Pendiente de GEN-06.
    }

    @Override
    public void exigirPermisoLeerPeriodos() {
        // Pendiente de GEN-06.
    }
}
