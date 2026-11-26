package com.universidad.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

// GEN-06-BE: cuentas, sesiones y filtro Bearer contra PostgreSQL, con datos ficticios
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadIntegrationTest {

    private static final String CONTRASENA = "clave-ficticia-123";

    @Autowired private MockMvc mvc;

    @Autowired private CuentaRepository cuentaRepository;

    @Autowired private HashContrasenasPort hash;

    @Autowired private AutenticacionService autenticacion;

    @Test
    void guardaLaCuentaConSusRolesYLaBuscaSinDistinguirMayusculas() {

        String correo = correoUnico();
        Cuenta guardada = crearCuenta(correo, EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));

        Cuenta encontrada = cuentaRepository.buscarPorCorreo(correo.toUpperCase()).orElseThrow();

        assertThat(encontrada.getIdCuenta()).isEqualTo(guardada.getIdCuenta());
        assertThat(encontrada.getRoles()).containsExactly(Rol.SUJETO);
        assertThat(encontrada.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
        assertThat(encontrada.getResumenContrasena()).doesNotContain(CONTRASENA);
        assertThat(cuentaRepository.existePorCorreo(correo.toUpperCase())).isTrue();
    }

    @Test
    void elCorreoEsUnicoSinDistinguirMayusculas() {

        String correo = correoUnico();
        crearCuenta(correo, EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> crearCuenta(correo.toUpperCase(), EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO)));
    }

    @Test
    void abreValidaYCierraUnaSesionGuardada() {

        String correo = correoUnico();
        Cuenta cuenta = crearCuenta(correo, EstadoCuenta.ACTIVA, Set.of(Rol.PSICOLOGO));

        String token = autenticacion.abrirSesion(correo, CONTRASENA.toCharArray()).token();

        assertThat(autenticacion.validar(token).orElseThrow().idCuenta())
                .isEqualTo(cuenta.getIdCuenta());

        autenticacion.cerrarSesion(token);

        assertThat(autenticacion.validar(token)).isEmpty();
    }

    @Test
    void elFiltroExigeSesionEnLaApiYDejaPasarConTokenValido() throws Exception {

        String correo = correoUnico();
        crearCuenta(correo, EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));
        String token = autenticacion.abrirSesion(correo, CONTRASENA.toCharArray()).token();

        mvc.perform(get("/api/v1/tests"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));

        mvc.perform(get("/api/v1/tests").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        autenticacion.cerrarSesion(token);

        mvc.perform(get("/api/v1/tests").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lasRutasPublicasDeAccesoNoExigenToken() throws Exception {

        // E13, E14, E16 y E17 no tienen controlador todavia (GEN-02-BE): el filtro no responde 401
        mvc.perform(post("/api/v1/auth/sesion").contentType("application/json").content("{}"))
                .andExpect(
                        result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(401));
    }

    @Test
    void elHealthDeActuatorNoExigeToken() throws Exception {

        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    private Cuenta crearCuenta(String correo, EstadoCuenta estado, Set<Rol> roles) {
        return cuentaRepository.guardar(
                new Cuenta(
                        null,
                        correo,
                        hash.crear(CONTRASENA.toCharArray()),
                        "Nombre",
                        "Ficticio",
                        estado,
                        roles));
    }

    private static String correoUnico() {
        return "cuenta-" + UUID.randomUUID() + "@ejemplo.test";
    }
}
