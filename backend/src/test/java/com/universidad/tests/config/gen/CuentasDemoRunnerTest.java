package com.universidad.tests.config.gen;

import static org.assertj.core.api.Assertions.assertThat;

import com.universidad.tests.domain.model.Cuenta;
import com.universidad.tests.domain.model.EstadoCuenta;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.port.HashContrasenasPort;
import com.universidad.tests.domain.repository.CuentaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.MapPropertySource;

// GEN-34-BE: cuentas ficticias por rol solo con el perfil demo
class CuentasDemoRunnerTest {

    private static final String CONTRASENA = "clave-ficticia-demo";

    @Test
    void conPerfilDemoCreaUnaCuentaActivaPorRol() {
        CuentasEnMemoria cuentas = new CuentasEnMemoria();

        new CuentasDemoRunner(cuentas, new HashFalso(), CONTRASENA).run(null);

        assertThat(cuentas.guardadas).hasSize(5);
        assertThat(cuentas.guardadas)
                .extracting(Cuenta::getCorreo)
                .containsExactlyInAnyOrder(
                        "sujeto@ejemplo.test",
                        "academico@ejemplo.test",
                        "psicologo@ejemplo.test",
                        "coordinador@ejemplo.test",
                        "decano@ejemplo.test");
        assertThat(cuentas.guardadas.stream().flatMap(c -> c.getRoles().stream()))
                .containsExactlyInAnyOrder(Rol.values());
        assertThat(cuentas.guardadas).allMatch(Cuenta::estaActiva);
        assertThat(cuentas.guardadas).allMatch(c -> !c.getResumenContrasena().contains(CONTRASENA));
    }

    @Test
    void noDuplicaNiModificaCuentasQueYaExisten() {
        CuentasEnMemoria cuentas = new CuentasEnMemoria();
        CuentasDemoRunner runner = new CuentasDemoRunner(cuentas, new HashFalso(), CONTRASENA);

        runner.run(null);
        runner.run(null);

        assertThat(cuentas.guardadas).hasSize(5);
    }

    @Test
    void sinDemoPasswordNoCreaNada() {
        CuentasEnMemoria cuentas = new CuentasEnMemoria();

        new CuentasDemoRunner(cuentas, new HashFalso(), "").run(null);

        assertThat(cuentas.guardadas).isEmpty();
    }

    @Test
    void soloSeRegistraConElPerfilDemo() {
        Profile perfil = CuentasDemoRunner.class.getAnnotation(Profile.class);

        assertThat(perfil).isNotNull();
        assertThat(perfil.value()).containsExactly("demo");
    }

    @Test
    void springNoRegistraElRunnerSinPerfilDemo() {
        try (AnnotationConfigApplicationContext contexto =
                new AnnotationConfigApplicationContext()) {
            contexto.register(CuentasDemoRunner.class);
            contexto.refresh();

            assertThat(contexto.getBeansOfType(CuentasDemoRunner.class)).isEmpty();
        }
    }

    @Test
    void springConPerfilDemoInyectaLaConfiguracionYCreaLasCuentasUnaSolaVez() {
        CuentasEnMemoria cuentas = new CuentasEnMemoria();
        try (AnnotationConfigApplicationContext contexto =
                new AnnotationConfigApplicationContext()) {
            contexto.getEnvironment().setActiveProfiles("demo");
            contexto.getEnvironment()
                    .getPropertySources()
                    .addFirst(
                            new MapPropertySource(
                                    "prueba-demo", Map.of("DEMO_PASSWORD", CONTRASENA)));
            contexto.registerBean(CuentaRepository.class, () -> cuentas);
            contexto.registerBean(HashContrasenasPort.class, HashFalso::new);
            contexto.register(CuentasDemoRunner.class);
            contexto.refresh();

            CuentasDemoRunner runner = contexto.getBean(CuentasDemoRunner.class);
            runner.run(null);
            List<Cuenta> originales = List.copyOf(cuentas.guardadas);
            runner.run(null);

            assertThat(cuentas.guardadas).hasSize(5).containsExactlyElementsOf(originales);
        }
    }

    private static final class HashFalso implements HashContrasenasPort {
        @Override
        public String crear(char[] contrasena) {
            return "resumen-" + new String(contrasena).hashCode();
        }

        @Override
        public boolean verificar(char[] contrasena, String resumen) {
            return crear(contrasena).equals(resumen);
        }
    }

    private static final class CuentasEnMemoria implements CuentaRepository {
        final List<Cuenta> guardadas = new ArrayList<>();

        @Override
        public Optional<Cuenta> buscarPorId(Long idCuenta) {
            return guardadas.stream().filter(c -> c.getIdCuenta().equals(idCuenta)).findFirst();
        }

        @Override
        public Optional<Cuenta> buscarPorCorreo(String correo) {
            return guardadas.stream()
                    .filter(c -> c.getCorreo().equalsIgnoreCase(correo))
                    .findFirst();
        }

        @Override
        public boolean existePorCorreo(String correo) {
            return buscarPorCorreo(correo).isPresent();
        }

        @Override
        public Cuenta guardar(Cuenta cuenta) {
            Cuenta guardada =
                    new Cuenta(
                            (long) guardadas.size() + 1,
                            cuenta.getCorreo(),
                            cuenta.getResumenContrasena(),
                            cuenta.getNombres(),
                            cuenta.getApellidos(),
                            EstadoCuenta.ACTIVA,
                            Set.copyOf(cuenta.getRoles()));
            guardadas.add(guardada);
            return guardada;
        }
    }
}
