package com.universidad.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.universidad.tests.application.service.AutorizacionContenidoService;
import com.universidad.tests.application.service.AutorizacionService;
import com.universidad.tests.application.service.ContextoSesion;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.domain.exception.NoAutenticadoException;
import com.universidad.tests.domain.exception.SinPermisoException;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

// GEN-06-BE: Documento general §2.3, lo no concedido se deniega
class AutorizacionServiceTest {

    private final AutorizacionService autorizacion = new AutorizacionService();
    private final AutorizacionContenidoService contenido =
            new AutorizacionContenidoService(autorizacion);

    @AfterEach
    void limpiar() {
        ContextoSesion.limpiar();
    }

    @Test
    void sinSesionRespondeNoAutenticado() {

        assertThrows(NoAutenticadoException.class, autorizacion::exigirSesion);
        assertThrows(NoAutenticadoException.class, () -> autorizacion.exigirPermiso("X"));
        assertThrows(NoAutenticadoException.class, () -> contenido.exigirPermisoPublicar(1L));
    }

    @Test
    void conSesionPeroSinElPermisoRespondeSinPermiso() {

        sesionCon(Set.of("PRACTICA_REALIZAR"));

        assertThrows(SinPermisoException.class, () -> autorizacion.exigirPermiso("CONFIGURAR_BFA"));
        assertThrows(SinPermisoException.class, () -> autorizacion.exigirPermiso(null));
    }

    @Test
    void conElPermisoExplicitoAutoriza() {

        sesionCon(Set.of("CONFIGURAR_BFA"));

        assertThat(autorizacion.exigirPermiso("CONFIGURAR_BFA").idCuenta()).isEqualTo(7L);
    }

    @Test
    void contenidoUsaLosPermisosDelContrato() {

        sesionCon(Set.of("CONFIGURAR_BFA"));
        assertDoesNotThrow(() -> contenido.exigirPermisoCrearVersion(1L));
        assertDoesNotThrow(() -> contenido.exigirPermisoPublicar(1L));
        assertThrows(SinPermisoException.class, () -> contenido.exigirPermisoEditarContenido(1L));

        sesionCon(Set.of("CONTENIDO_EDITAR"));
        assertDoesNotThrow(() -> contenido.exigirPermisoEditarContenido(1L));
        assertThrows(SinPermisoException.class, () -> contenido.exigirPermisoCrearVersion(1L));
    }

    @Test
    void retirarYConsultarContenidoSiguenDenegadosSinPermisoDocumentado() {

        sesionCon(Set.of("CONFIGURAR_BFA", "CONTENIDO_EDITAR", "OPCIONES_EDITAR"));

        assertThrows(SinPermisoException.class, () -> contenido.exigirPermisoRetirar(1L));
        assertThrows(
                SinPermisoException.class, () -> contenido.exigirPermisoConsultarContenido(1L));
    }

    private void sesionCon(Set<String> permisos) {
        ContextoSesion.establecer(new IdentidadSesion(7L, 3L, "COORDINADOR", permisos));
    }
}
