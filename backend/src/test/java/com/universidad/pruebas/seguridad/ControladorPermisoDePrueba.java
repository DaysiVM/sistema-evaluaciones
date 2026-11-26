package com.universidad.pruebas.seguridad;

import com.universidad.tests.application.service.AutorizacionService;
import com.universidad.tests.domain.model.MatrizPermisos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Solo para pruebas (fuera del paquete escaneado por la aplicacion): un endpoint de /api que
// exige CONFIGURAR_BFA, como E25-E31.
@RestController
public class ControladorPermisoDePrueba {

    private final AutorizacionService autorizacion;

    public ControladorPermisoDePrueba(AutorizacionService autorizacion) {
        this.autorizacion = autorizacion;
    }

    @GetMapping(value = "/api/v1/prueba-permiso", produces = "application/json")
    public String operacionProtegida() {
        autorizacion.exigirPermiso(MatrizPermisos.CONFIGURAR_BFA);
        return "{\"ok\":true}";
    }
}
