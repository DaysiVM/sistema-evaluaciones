package com.universidad.tests.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.universidad.tests.domain.model.Cuenta;
import com.universidad.tests.domain.model.EstadoCuenta;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.port.HashContrasenasPort;
import com.universidad.tests.domain.repository.CuentaRepository;
import com.universidad.tests.dto.request.RegistroRequest;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class RegistrarCuentaServiceTest {

    @Mock private CuentaRepository cuentas;
    @Mock private HashContrasenasPort hash;
    @InjectMocks private RegistrarCuentaService registro;

    @Test
    void registrar_cuentaBasica_guardaSoloResumenYLimpiaArreglo() {
        AtomicReference<char[]> credencial = new AtomicReference<>();
        when(hash.crear(any()))
                .thenAnswer(
                        invocacion -> {
                            char[] recibida = invocacion.getArgument(0);
                            assertThat(recibida).containsExactly(" clave ficticia ".toCharArray());
                            credencial.set(recibida);
                            return "resumen-ficticio";
                        });
        when(cuentas.guardar(any()))
                .thenReturn(
                        new Cuenta(
                                7L,
                                "persona@example.test",
                                "resumen-ficticio",
                                "Persona",
                                "Ficticia",
                                EstadoCuenta.ACTIVA,
                                Set.of(Rol.SUJETO)));

        var respuesta = registro.registrar(solicitud("SUJETO"));

        assertThat(respuesta.id()).isEqualTo(7L);
        assertThat(respuesta.rol()).isEqualTo("SUJETO");
        assertThat(respuesta.estado()).isEqualTo("ACTIVA");
        ArgumentCaptor<Cuenta> guardada = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentas).guardar(guardada.capture());
        assertThat(guardada.getValue().getResumenContrasena()).isEqualTo("resumen-ficticio");
        assertThat(guardada.getValue().getRoles()).containsExactly(Rol.SUJETO);
        assertThat(credencial.get()).containsOnly('\0');
    }

    @ParameterizedTest
    @ValueSource(strings = {"ACADEMICO", "COORDINADOR", "PSICOLOGO", "DECANO", "ADMIN", "sujeto"})
    void registrar_rolSinAsignacionAutorizada_noPersisteNiCalculaHash(String rol) {
        assertThatThrownBy(() -> registro.registrar(solicitud(rol)))
                .isInstanceOfSatisfying(
                        RegistroCuentaException.class,
                        ex ->
                                assertThat(ex.getCodigo())
                                        .isEqualTo(
                                                RegistroCuentaException.Codigo.ROL_NO_AUTORIZADO));
        verifyNoInteractions(cuentas, hash);
    }

    @Test
    void registrar_nombresVacios_noPersisteNiCalculaHash() {
        RegistroRequest solicitud =
                new RegistroRequest("persona@example.test", "ficticia", " ", "Ficticia", "SUJETO");

        assertThatThrownBy(() -> registro.registrar(solicitud))
                .isInstanceOfSatisfying(
                        RegistroCuentaException.class,
                        ex ->
                                assertThat(ex.getCodigo())
                                        .isEqualTo(
                                                RegistroCuentaException.Codigo.SOLICITUD_INVALIDA));
        verifyNoInteractions(cuentas, hash);
    }

    @Test
    void registrar_correoExistente_noCalculaHashNiGuarda() {
        when(cuentas.existePorCorreo("persona@example.test")).thenReturn(true);

        assertThatThrownBy(() -> registro.registrar(solicitud("SUJETO")))
                .isInstanceOfSatisfying(
                        RegistroCuentaException.class,
                        ex ->
                                assertThat(ex.getCodigo())
                                        .isEqualTo(
                                                RegistroCuentaException.Codigo.CUENTA_EXISTENTE));
        verifyNoInteractions(hash);
    }

    @Test
    void registrar_carreraDeCorreoDuplicado_traduceSoloRestriccionDeCorreo() {
        when(hash.crear(any())).thenReturn("resumen-ficticio");
        when(cuentas.guardar(any()))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "conflicto",
                                new SQLException(
                                        "duplicate key violates uq_cuenta_correo", "23505")));

        assertThatThrownBy(() -> registro.registrar(solicitud("SUJETO")))
                .isInstanceOfSatisfying(
                        RegistroCuentaException.class,
                        ex ->
                                assertThat(ex.getCodigo())
                                        .isEqualTo(
                                                RegistroCuentaException.Codigo.CUENTA_EXISTENTE));
    }

    @Test
    void registrar_otraRestriccion_noLaConfundeConCorreoDuplicado() {
        when(hash.crear(any())).thenReturn("resumen-ficticio");
        DataIntegrityViolationException fallo =
                new DataIntegrityViolationException(
                        "conflicto", new SQLException("otra restriccion", "23514"));
        when(cuentas.guardar(any())).thenThrow(fallo);

        assertThatThrownBy(() -> registro.registrar(solicitud("SUJETO"))).isSameAs(fallo);
    }

    @Test
    void registrar_contrasenaNoCodificable_limpiaArregloYDevuelveSolicitudInvalida() {
        AtomicReference<char[]> credencial = new AtomicReference<>();
        when(hash.crear(any()))
                .thenAnswer(
                        invocacion -> {
                            credencial.set(invocacion.getArgument(0));
                            throw new IllegalArgumentException("codificacion invalida");
                        });

        assertThatThrownBy(() -> registro.registrar(solicitud("SUJETO")))
                .isInstanceOfSatisfying(
                        RegistroCuentaException.class,
                        ex ->
                                assertThat(ex.getCodigo())
                                        .isEqualTo(
                                                RegistroCuentaException.Codigo.SOLICITUD_INVALIDA));
        assertThat(credencial.get()).containsOnly('\0');
    }

    private RegistroRequest solicitud(String rol) {
        return new RegistroRequest(
                "persona@example.test", " clave ficticia ", "Persona", "Ficticia", rol);
    }
}
