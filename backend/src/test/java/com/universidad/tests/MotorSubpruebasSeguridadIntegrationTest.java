package com.universidad.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.domain.model.Cuenta;
import com.universidad.tests.domain.model.EstadoCuenta;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.port.HashContrasenasPort;
import com.universidad.tests.domain.repository.CuentaRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MotorSubpruebasSeguridadIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private CuentaRepository cuentas;
    @Autowired private HashContrasenasPort hash;
    @Autowired private AutenticacionService autenticacion;

    @Test
    void e5e6e7Responden401SinSesionY403ConSesionSinPermiso() throws Exception {
        String correo = "motor-" + UUID.randomUUID() + "@ejemplo.test";
        String contrasena = "clave-ficticia-de-prueba";
        cuentas.guardar(
                new Cuenta(
                        null,
                        correo,
                        hash.crear(contrasena.toCharArray()),
                        "Sujeto",
                        "Ficticio",
                        EstadoCuenta.ACTIVA,
                        Set.of(Rol.DECANO)));
        String token = autenticacion.abrirSesion(correo, contrasena.toCharArray()).token();

        String ruta = "/api/v1/evaluaciones/" + Long.MAX_VALUE + "/subpruebas/SUB";
        var solicitudes =
                List.of(
                        post(ruta + "/iniciar"),
                        get(ruta + "/preguntas"),
                        put(ruta + "/respuestas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                {"respuestas":[{"preguntaId":1,"opcion":"A"}]}
                                """));
        for (var solicitud : solicitudes) {
            mvc.perform(solicitud)
                    .andExpect(status().isUnauthorized())
                    .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                    .andExpect(jsonPath("$.codigo").value("NO_AUTENTICADO"));
            mvc.perform(solicitud.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.codigo").value("SIN_PERMISO"));
        }
    }
}
