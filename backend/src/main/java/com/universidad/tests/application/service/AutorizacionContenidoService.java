package com.universidad.tests.application.service;

import static com.universidad.tests.domain.model.MatrizPermisos.CONFIGURAR_BFA;
import static com.universidad.tests.domain.model.MatrizPermisos.CONTENIDO_EDITAR;

import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import org.springframework.stereotype.Service;

// GEN-06-BE: reemplaza a AutorizacionContenidoDenegadaAdapter con los permisos del contrato
// (docs/api/openapi.yaml): E25 y E31 piden CONFIGURAR_BFA; E27 pide CONTENIDO_EDITAR.
// Retirar y consultar contenido restringido no tienen permiso documentado: siguen denegados.
@Service
public class AutorizacionContenidoService implements AutorizacionContenidoPort {

    private final AutorizacionService autorizacion;

    public AutorizacionContenidoService(AutorizacionService autorizacion) {
        this.autorizacion = autorizacion;
    }

    @Override
    public void exigirPermisoCrearVersion(Long idTest) {
        autorizacion.exigirPermiso(CONFIGURAR_BFA);
    }

    @Override
    public void exigirPermisoEditarContenido(Long idVersionFormulario) {
        autorizacion.exigirPermiso(CONTENIDO_EDITAR);
    }

    @Override
    public void exigirPermisoConfigurarSubprueba(Long idVersionFormulario) {
        autorizacion.exigirPermiso(CONFIGURAR_BFA);
        autorizacion.exigirPermiso("INSTRUCCIONES_EDITAR");
    }

    @Override
    public void exigirPermisoPublicar(Long idVersionFormulario) {
        autorizacion.exigirPermiso(CONFIGURAR_BFA);
    }

    @Override
    public void exigirPermisoRetirar(Long idVersionFormulario) {
        autorizacion.denegarPendiente();
    }

    @Override
    public void exigirPermisoConsultarContenido(Long idVersionFormulario) {
        autorizacion.denegarPendiente();
    }
}
