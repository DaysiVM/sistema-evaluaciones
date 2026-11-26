package com.universidad.tests.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.universidad.tests.domain.model.*;
import com.universidad.tests.domain.port.*;
import com.universidad.tests.domain.repository.*;
import java.time.*;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VigenciaSesionTest {
    @Test
    void expiraExactamenteAlCumplirLaDuracionConfigurada() {
        var cuentas = mock(CuentaRepository.class);
        var sesiones = mock(SesionRepository.class);
        var hash = mock(HashContrasenasPort.class);
        var tokens = mock(TokensSesionPort.class);
        Clock reloj = mock(Clock.class);
        Instant inicio = Instant.parse("2026-11-25T10:00:00Z");
        when(reloj.instant()).thenReturn(inicio);
        when(tokens.generar()).thenReturn("valor-sintetico");
        when(tokens.resumirSiValido(any())).thenReturn("resumen-sintetico");
        when(hash.crear(any())).thenReturn("hash-sintetico");
        when(hash.verificar(any(), any())).thenReturn(true);
        var cuenta =
                new Cuenta(
                        1L,
                        "fixture@ejemplo.test",
                        "hash",
                        "Persona",
                        "Ficticia",
                        EstadoCuenta.ACTIVA,
                        Set.of(Rol.SUJETO));
        when(cuentas.buscarPorCorreo(any())).thenReturn(Optional.of(cuenta));
        when(cuentas.buscarPorId(1L)).thenReturn(Optional.of(cuenta));
        when(sesiones.guardar(any()))
                .thenAnswer(
                        i -> {
                            Sesion sesion = i.getArgument(0);
                            when(sesiones.buscarPorResumenToken(any()))
                                    .thenReturn(Optional.of(sesion));
                            return sesion;
                        });
        var servicio =
                new AutenticacionService(
                        cuentas,
                        sesiones,
                        hash,
                        tokens,
                        MatrizPermisos.aprobada(),
                        reloj,
                        Duration.ofMinutes(30));
        var abierta = servicio.abrirSesion("fixture@ejemplo.test", new char[] {'x'});
        assertThat(abierta.expiraEn()).isEqualTo(inicio.plusSeconds(1800));
        when(reloj.instant()).thenReturn(inicio.plusSeconds(1799));
        assertThat(servicio.validar(abierta.token())).isPresent();
        when(reloj.instant()).thenReturn(inicio.plusSeconds(1800));
        assertThat(servicio.validar(abierta.token())).isEmpty();
        when(reloj.instant()).thenReturn(inicio.plusSeconds(1801));
        assertThat(servicio.validar(abierta.token())).isEmpty();
    }

    @Test
    void noAdmiteDuracionNulaNiNoPositiva() {
        for (Duration duracion : new Duration[] {null, Duration.ZERO, Duration.ofSeconds(-1)}) {
            assertThrows(
                    IllegalArgumentException.class,
                    () ->
                            new AutenticacionService(
                                    null, null, null, null, null, Clock.systemUTC(), duracion));
        }
    }
}
