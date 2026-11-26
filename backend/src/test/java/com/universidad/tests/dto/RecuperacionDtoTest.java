package com.universidad.tests.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.tests.dto.request.ConfirmarRecuperacionRequest;
import com.universidad.tests.dto.request.RecuperacionRequest;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

class RecuperacionDtoTest {

    private final ObjectMapper json = new ObjectMapper();

    @Test
    void confirmar_entradaContrato_leeCredencialesSinSerializarlasNiMostrarlasEnToString()
            throws Exception {
        ConfirmarRecuperacionRequest solicitud =
                json.readValue(
                        """
                {"token":"token-ficticio","nuevaContrasena":"clave ficticia"}
                """,
                        ConfirmarRecuperacionRequest.class);

        assertThat(solicitud.token()).isEqualTo("token-ficticio");
        assertThat(solicitud.nuevaContrasena()).isEqualTo("clave ficticia");
        assertThat(json.writeValueAsString(solicitud)).isEqualTo("{}");
        assertThat(solicitud.toString()).doesNotContain("token-ficticio", "clave ficticia");
    }

    @Test
    void pedirRecuperacion_correoContrato_seDeserializaYToStringNoExponeDatos() throws Exception {
        RecuperacionRequest solicitud =
                json.readValue("{\"correo\":\"persona@example.test\"}", RecuperacionRequest.class);

        assertThat(solicitud.correo()).isEqualTo("persona@example.test");
        assertThat(solicitud.toString()).doesNotContain(solicitud.correo());
    }

    @Test
    void validar_camposObligatoriosYCorreo_rechazaNulosYFormatoIncorrecto() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertThat(validator.validate(new RecuperacionRequest("invalido"))).hasSize(1);
            assertThat(validator.validate(new ConfirmarRecuperacionRequest(null, null))).hasSize(2);
        }
    }
}
