package com.universidad.tests.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.universidad.pruebas.seguridad.ControladorPermisoDePrueba;
import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.AutorizacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.domain.model.MatrizPermisos;
import com.universidad.tests.domain.model.Rol;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// GEN-33-BE: un rol sin el permiso recibe 403 SIN_PERMISO; con el permiso pasa
class PermisosHttpTest {

    private final MatrizPermisos matriz = MatrizPermisos.aprobada();
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        AutenticacionService autenticacion = mock(AutenticacionService.class);
        when(autenticacion.validar(anyString())).thenReturn(Optional.empty());
        for (Rol rol : Rol.values()) {
            when(autenticacion.validar("token-" + rol.name()))
                    .thenReturn(
                            Optional.of(
                                    new IdentidadSesion(
                                            1L, 1L, rol.name(), matriz.permisosDe(rol))));
        }
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        mvc =
                MockMvcBuilders.standaloneSetup(
                                new ControladorPermisoDePrueba(new AutorizacionService()))
                        .setControllerAdvice(new ManejadorErroresSeguridad())
                        .addFilters(new FiltroSesionBearer(autenticacion, objectMapper))
                        .build();
    }

    @Test
    void unRolSinElPermisoRecibe403SinPermiso() throws Exception {
        for (Rol rol : new Rol[] {Rol.SUJETO, Rol.ACADEMICO, Rol.PSICOLOGO, Rol.DECANO}) {
            mvc.perform(get("/api/v1/prueba-permiso").header("Authorization", bearer(rol)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"))
                    .andExpect(jsonPath("$.ruta").value("/api/v1/prueba-permiso"));
        }
    }

    @Test
    void elRolConElPermisoPasa() throws Exception {
        mvc.perform(get("/api/v1/prueba-permiso").header("Authorization", bearer(Rol.COORDINADOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    void sinTokenSigueSiendo401() throws Exception {
        mvc.perform(get("/api/v1/prueba-permiso"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }

    private static String bearer(Rol rol) {
        return "Bearer token-" + rol.name();
    }
}
