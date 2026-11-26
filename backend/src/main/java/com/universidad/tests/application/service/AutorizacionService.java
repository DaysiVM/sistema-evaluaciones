package com.universidad.tests.application.service;

import com.universidad.tests.domain.exception.NoAutenticadoException;
import com.universidad.tests.domain.exception.SinPermisoException;
import org.springframework.stereotype.Service;

// GEN-06-BE: politica de autorizacion del Documento general §2.3.
// Toda operacion protegida exige sesion valida, rol activo y permiso explicito;
// lo no concedido se deniega.
@Service
public class AutorizacionService {

    public IdentidadSesion exigirSesion() {
        return ContextoSesion.actual().orElseThrow(NoAutenticadoException::new);
    }

    public IdentidadSesion exigirPermiso(String permiso) {
        IdentidadSesion identidad = exigirSesion();
        if (permiso == null || !identidad.tienePermiso(permiso)) {
            throw new SinPermisoException();
        }
        return identidad;
    }

    // Para operaciones cuyo permiso aun no esta documentado: se exige sesion y se deniega
    public void denegarPendiente() {
        exigirSesion();
        throw new SinPermisoException();
    }
}
