package com.universidad.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.pruebas.seguridad.ControladorPermisoDePrueba;
import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.domain.model.Cuenta;
import com.universidad.tests.domain.model.EstadoCuenta;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.port.HashContrasenasPort;
import com.universidad.tests.domain.repository.CuentaRepository;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// Cuentas y sesiones reales en PostgreSQL, sin simular AutenticacionService.
// El controlador importado existe solo en este contexto de prueba, no en la API desplegada.
@SpringBootTest
@AutoConfigureMockMvc
@Import(ControladorPermisoDePrueba.class)
@Transactional
class PermisosSesionIntegrationTest {

    private static final String CLAVE_FICTICIA = "prueba-permisos-2026";
    private static final String RUTA = "/api/v1/prueba-permiso";

    @Autowired private MockMvc mvc;
    @Autowired private CuentaRepository cuentas;
    @Autowired private HashContrasenasPort hash;
    @Autowired private AutenticacionService autenticacion;

    @Test
    void cuatroRolesSinConfigurarBfaReciben403ConSesionReal() throws Exception {
        for (Rol rol : new Rol[] {Rol.SUJETO, Rol.ACADEMICO, Rol.PSICOLOGO, Rol.DECANO}) {
            String token = abrirSesion(rol);
            mvc.perform(get(RUTA).header("Authorization", "Bearer " + token))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"))
                    .andExpect(jsonPath("$.ruta").value(RUTA));
        }
    }

    @Test
    void coordinadorConSesionRealPuedeConfigurarBfa() throws Exception {
        String token = abrirSesion(Rol.COORDINADOR);
        mvc.perform(get(RUTA).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    void sinTokenOSesionCerradaResponde401() throws Exception {
        mvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));

        String token = abrirSesion(Rol.COORDINADOR);
        autenticacion.cerrarSesion(token);
        mvc.perform(get(RUTA).header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
    }

    private String abrirSesion(Rol rol) {
        String correo = "permisos-" + UUID.randomUUID() + "@ejemplo.test";
        cuentas.guardar(
                new Cuenta(
                        null,
                        correo,
                        hash.crear(CLAVE_FICTICIA.toCharArray()),
                        "Nombre",
                        "Ficticio",
                        EstadoCuenta.ACTIVA,
                        Set.of(rol)));
        return autenticacion.abrirSesion(correo, CLAVE_FICTICIA.toCharArray()).token();
    }
}
