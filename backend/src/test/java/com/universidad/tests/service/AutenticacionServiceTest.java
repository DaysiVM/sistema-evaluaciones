package com.universidad.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.universidad.tests.application.service.AutenticacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.application.service.SesionAbierta;
import com.universidad.tests.domain.exception.CredencialesInvalidasException;
import com.universidad.tests.domain.exception.NoAutenticadoException;
import com.universidad.tests.domain.exception.RolActivoNoDeterminadoException;
import com.universidad.tests.domain.model.Cuenta;
import com.universidad.tests.domain.model.EstadoCuenta;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.model.Sesion;
import com.universidad.tests.domain.repository.CuentaRepository;
import com.universidad.tests.domain.repository.SesionRepository;
import com.universidad.tests.infrastructure.security.HashContrasenasAdapter;
import com.universidad.tests.infrastructure.security.TokensSesionAdapter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// GEN-06-BE: credenciales, sesiones y tokens con datos ficticios
class AutenticacionServiceTest {

    private static final String CONTRASENA = "clave-ficticia-123";

    private final HashContrasenasAdapter hash = new HashContrasenasAdapter();
    private final TokensSesionAdapter tokens = new TokensSesionAdapter();
    private CuentasEnMemoria cuentas;
    private SesionesEnMemoria sesiones;
    private AutenticacionService servicio;

    @BeforeEach
    void preparar() {
        cuentas = new CuentasEnMemoria();
        sesiones = new SesionesEnMemoria();
        servicio = new AutenticacionService(cuentas, sesiones, hash, tokens);
    }

    @Test
    void abreSesionConCredencialesValidasYGuardaSoloElResumenDelToken() {

        Cuenta cuenta = crearCuenta("ana@ejemplo.test", EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));

        SesionAbierta sesion = servicio.abrirSesion("ana@ejemplo.test", CONTRASENA.toCharArray());

        assertThat(sesion.token()).isNotBlank();
        assertThat(sesion.rolActivo()).isEqualTo("SUJETO");
        assertThat(sesion.permisos())
                .containsExactly(
                        "EVALUACION_FINALIZAR",
                        "ITEMS_LEER",
                        "PERIODO_LEER",
                        "RECUPERAR_INTENTO",
                        "RESPUESTA_ESCRIBIR",
                        "SUBPRUEBA_INICIAR",
                        "SUJETO_LEER");
        assertThat(sesion.expiraEn()).isAfter(Instant.now());
        assertThat(sesiones.guardadas).hasSize(1);
        Sesion guardada = sesiones.guardadas.get(0);
        assertThat(guardada.getExpiraEn()).isEqualTo(sesion.expiraEn());
        assertThat(guardada.getExpiraEn()).isEqualTo(guardada.getAbiertaEn().plusSeconds(7200));
        assertThat(guardada.getIdCuenta()).isEqualTo(cuenta.getIdCuenta());
        assertThat(guardada.getResumenToken())
                .isNotEqualTo(sesion.token())
                .isEqualTo(tokens.resumirSiValido(sesion.token()));
    }

    @Test
    void elCorreoNoDistingueMayusculas() {

        crearCuenta("ana@ejemplo.test", EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));

        assertThat(servicio.abrirSesion("ANA@Ejemplo.TEST", CONTRASENA.toCharArray()).token())
                .isNotBlank();
    }

    @Test
    void rechazaContrasenaIncorrectaCorreoInexistenteYCuentaInhabilitadaIgual() {

        crearCuenta("ana@ejemplo.test", EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));
        crearCuenta("inactiva@ejemplo.test", EstadoCuenta.INHABILITADA, Set.of(Rol.SUJETO));

        CredencialesInvalidasException incorrecta =
                assertThrows(
                        CredencialesInvalidasException.class,
                        () -> servicio.abrirSesion("ana@ejemplo.test", "otra".toCharArray()));
        CredencialesInvalidasException inexistente =
                assertThrows(
                        CredencialesInvalidasException.class,
                        () -> servicio.abrirSesion("nadie@ejemplo.test", CONTRASENA.toCharArray()));
        CredencialesInvalidasException inhabilitada =
                assertThrows(
                        CredencialesInvalidasException.class,
                        () ->
                                servicio.abrirSesion(
                                        "inactiva@ejemplo.test", CONTRASENA.toCharArray()));

        assertThat(incorrecta.getMessage())
                .isEqualTo(inexistente.getMessage())
                .isEqualTo(inhabilitada.getMessage());
        assertThat(sesiones.guardadas).isEmpty();
    }

    @Test
    void rechazaCredencialesNulas() {

        assertThrows(
                CredencialesInvalidasException.class,
                () -> servicio.abrirSesion("ana@ejemplo.test", null));
        assertThrows(
                CredencialesInvalidasException.class,
                () -> servicio.abrirSesion(null, CONTRASENA.toCharArray()));
    }

    @Test
    void cuentaConVariosRolesNoAbreSesionMientrasNoSeDefinaElRolActivo() {

        crearCuenta(
                "varios@ejemplo.test", EstadoCuenta.ACTIVA, Set.of(Rol.ACADEMICO, Rol.PSICOLOGO));

        assertThrows(
                RolActivoNoDeterminadoException.class,
                () -> servicio.abrirSesion("varios@ejemplo.test", CONTRASENA.toCharArray()));
        assertThat(sesiones.guardadas).isEmpty();
    }

    @Test
    void validaElTokenDeUnaSesionAbierta() {

        Cuenta cuenta =
                crearCuenta("ana@ejemplo.test", EstadoCuenta.ACTIVA, Set.of(Rol.COORDINADOR));
        String token = servicio.abrirSesion("ana@ejemplo.test", CONTRASENA.toCharArray()).token();

        IdentidadSesion identidad = servicio.validar(token).orElseThrow();

        assertThat(identidad.idCuenta()).isEqualTo(cuenta.getIdCuenta());
        assertThat(identidad.rolActivo()).isEqualTo("COORDINADOR");
        assertThat(identidad.permisos()).contains("CONFIGURAR_BFA", "PERIODO_CREAR");
        assertThat(identidad.permisos()).doesNotContain("REVISAR_FLUIDEZ", "RECALCULAR_BFA");
    }

    @Test
    void rechazaTokensDesconocidosMalformadosOVacios() {

        assertThat(servicio.validar(tokens.generar())).isEmpty();
        assertThat(servicio.validar("no-es-un-token")).isEmpty();
        assertThat(servicio.validar("")).isEmpty();
        assertThat(servicio.validar(null)).isEmpty();
    }

    @Test
    void cerrarSesionInvalidaElToken() {

        crearCuenta("ana@ejemplo.test", EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));
        String token = servicio.abrirSesion("ana@ejemplo.test", CONTRASENA.toCharArray()).token();

        servicio.cerrarSesion(token);

        assertThat(servicio.validar(token)).isEmpty();
        assertThrows(NoAutenticadoException.class, () -> servicio.cerrarSesion(token));
    }

    @Test
    void cerrarConTokenInvalidoRespondeNoAutenticado() {

        assertThrows(NoAutenticadoException.class, () -> servicio.cerrarSesion("x"));
        assertThrows(NoAutenticadoException.class, () -> servicio.cerrarSesion(tokens.generar()));
    }

    @Test
    void unaCuentaInhabilitadaDespuesDeAbrirSesionPierdeElAcceso() {

        Cuenta cuenta = crearCuenta("ana@ejemplo.test", EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));
        String token = servicio.abrirSesion("ana@ejemplo.test", CONTRASENA.toCharArray()).token();

        cuentas.guardar(
                new Cuenta(
                        cuenta.getIdCuenta(),
                        cuenta.getCorreo(),
                        cuenta.getResumenContrasena(),
                        cuenta.getNombres(),
                        cuenta.getApellidos(),
                        EstadoCuenta.INHABILITADA,
                        cuenta.getRoles()));

        assertThat(servicio.validar(token)).isEmpty();
    }

    @Test
    void unaSesionVencidaNoEsValida() {

        Cuenta cuenta = crearCuenta("ana@ejemplo.test", EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));
        String token = tokens.generar();
        Instant antes = Instant.now().minusSeconds(120);
        sesiones.guardar(
                new Sesion(
                        null,
                        cuenta.getIdCuenta(),
                        tokens.resumirSiValido(token),
                        Rol.SUJETO,
                        antes,
                        antes.plusSeconds(60),
                        null));

        assertThat(servicio.validar(token)).isEmpty();
    }

    @Test
    void laContrasenaNoSeGuardaEnTextoPlano() {

        Cuenta cuenta = crearCuenta("ana@ejemplo.test", EstadoCuenta.ACTIVA, Set.of(Rol.SUJETO));

        assertThat(cuenta.getResumenContrasena()).doesNotContain(CONTRASENA);
        assertThat(cuenta.toString()).doesNotContain(cuenta.getResumenContrasena());
    }

    private Cuenta crearCuenta(String correo, EstadoCuenta estado, Set<Rol> roles) {
        return cuentas.guardar(
                new Cuenta(
                        null,
                        correo,
                        hash.crear(CONTRASENA.toCharArray()),
                        "Nombre",
                        "Ficticio",
                        estado,
                        roles));
    }

    private static final class CuentasEnMemoria implements CuentaRepository {

        private final Map<Long, Cuenta> porId = new HashMap<>();
        private long siguienteId = 1;

        @Override
        public Optional<Cuenta> buscarPorId(Long idCuenta) {
            return Optional.ofNullable(porId.get(idCuenta));
        }

        @Override
        public Optional<Cuenta> buscarPorCorreo(String correo) {
            return porId.values().stream()
                    .filter(c -> c.getCorreo().equalsIgnoreCase(correo))
                    .findFirst();
        }

        @Override
        public boolean existePorCorreo(String correo) {
            return buscarPorCorreo(correo).isPresent();
        }

        @Override
        public Cuenta guardar(Cuenta cuenta) {
            Long id = cuenta.getIdCuenta() == null ? siguienteId++ : cuenta.getIdCuenta();
            Cuenta guardada =
                    new Cuenta(
                            id,
                            cuenta.getCorreo(),
                            cuenta.getResumenContrasena(),
                            cuenta.getNombres(),
                            cuenta.getApellidos(),
                            cuenta.getEstado(),
                            cuenta.getRoles());
            porId.put(id, guardada);
            return guardada;
        }
    }

    private static final class SesionesEnMemoria implements SesionRepository {

        private final List<Sesion> guardadas = new ArrayList<>();

        @Override
        public Sesion guardar(Sesion sesion) {
            Sesion guardada =
                    new Sesion(
                            (long) guardadas.size() + 1,
                            sesion.getIdCuenta(),
                            sesion.getResumenToken(),
                            sesion.getRolActivo(),
                            sesion.getAbiertaEn(),
                            sesion.getExpiraEn(),
                            sesion.getCerradaEn());
            guardadas.add(guardada);
            return guardada;
        }

        @Override
        public Optional<Sesion> buscarPorResumenToken(String resumenToken) {
            return guardadas.stream()
                    .filter(s -> s.getResumenToken().equals(resumenToken))
                    .findFirst();
        }

        @Override
        public boolean cerrar(String resumenToken, Instant cerradaEn) {
            for (int i = 0; i < guardadas.size(); i++) {
                Sesion s = guardadas.get(i);
                if (s.getResumenToken().equals(resumenToken) && s.getCerradaEn() == null) {
                    guardadas.set(
                            i,
                            new Sesion(
                                    s.getIdSesion(),
                                    s.getIdCuenta(),
                                    s.getResumenToken(),
                                    s.getRolActivo(),
                                    s.getAbiertaEn(),
                                    s.getExpiraEn(),
                                    cerradaEn));
                    return true;
                }
            }
            return false;
        }
    }
}
