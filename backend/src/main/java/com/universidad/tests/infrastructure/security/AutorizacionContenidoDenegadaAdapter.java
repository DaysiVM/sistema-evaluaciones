package com.universidad.tests.infrastructure.security;

import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!seguridad-integrada")
public class AutorizacionContenidoDenegadaAdapter implements AutorizacionContenidoPort {

    @Override
    public void exigirPermisoCrearVersion(Long idTest) {
        denegarAcceso();
    }

    @Override
    public void exigirPermisoEditarContenido(Long idVersionFormulario) {
        denegarAcceso();
    }

    @Override
    public void exigirPermisoPublicar(Long idVersionFormulario) {
        denegarAcceso();
    }

    @Override
    public void exigirPermisoRetirar(Long idVersionFormulario) {
        denegarAcceso();
    }

    @Override
    public void exigirPermisoConsultarContenido(Long idVersionFormulario) {
        denegarAcceso();
    }

    private void denegarAcceso() {
        throw new SecurityException(
                "Operacion no autorizada: "
                        + "el mecanismo de autenticacion "
                        + "y permisos aun no esta configurado");
    }
}
