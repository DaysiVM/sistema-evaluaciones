package com.universidad.tests.application.service;

import com.universidad.tests.domain.port.AutorizacionSujetoPort;
import org.springframework.stereotype.Service;

/** Permisos E18 y E23 del contrato; no depende del adaptador provisional de evaluaciones. */
@Service
public class AutorizacionSujetoService implements AutorizacionSujetoPort {
    private final AutorizacionService autorizacion;

    public AutorizacionSujetoService(AutorizacionService autorizacion) {
        this.autorizacion = autorizacion;
    }

    @Override
    public void exigirPermisoCrearSujeto() {
        autorizacion.exigirPermiso("SUJETO_CREAR");
    }

    @Override
    public void exigirPermisoBuscarSujetos() {
        autorizacion.exigirPermiso("SUJETO_LEER");
    }
}
