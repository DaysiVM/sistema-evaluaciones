package com.universidad.tests.config.gen;

import com.universidad.tests.domain.model.Cuenta;
import com.universidad.tests.domain.model.EstadoCuenta;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.port.HashContrasenasPort;
import com.universidad.tests.domain.repository.CuentaRepository;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * GEN-34-BE: crea una cuenta ficticia por rol al arrancar con el perfil demo, sin cargar datos con
 * migraciones. La contrasena sale de DEMO_PASSWORD y nunca se escribe en el codigo. Si una cuenta
 * ya existe no se toca. Nunca detiene el arranque.
 */
@Component
@Profile("demo")
public class CuentasDemoRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CuentasDemoRunner.class);

    static final Map<String, Rol> CUENTAS_DEMO = cuentasDemo();

    private final CuentaRepository cuentas;
    private final HashContrasenasPort hash;
    private final String contrasena;

    public CuentasDemoRunner(
            CuentaRepository cuentas,
            HashContrasenasPort hash,
            @Value("${DEMO_PASSWORD:}") String contrasena) {
        this.cuentas = cuentas;
        this.hash = hash;
        this.contrasena = contrasena;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (contrasena == null || contrasena.isBlank()) {
            LOG.warn("DEMO_PASSWORD no esta definida; no se crean las cuentas demo");
            return;
        }
        int creadas = 0;
        for (Map.Entry<String, Rol> entrada : CUENTAS_DEMO.entrySet()) {
            try {
                if (crearSiNoExiste(entrada.getKey(), entrada.getValue())) {
                    creadas++;
                }
            } catch (RuntimeException e) {
                LOG.warn("No se pudo crear la cuenta demo del rol {}", entrada.getValue(), e);
            }
        }
        LOG.info("Cuentas demo: {} creadas, {} en total", creadas, CUENTAS_DEMO.size());
    }

    private boolean crearSiNoExiste(String correo, Rol rol) {
        if (cuentas.existePorCorreo(correo)) {
            return false;
        }
        cuentas.guardar(
                new Cuenta(
                        null,
                        correo,
                        hash.crear(contrasena.toCharArray()),
                        "Demo",
                        nombreRol(rol),
                        EstadoCuenta.ACTIVA,
                        Set.of(rol)));
        return true;
    }

    private static String nombreRol(Rol rol) {
        String nombre = rol.name().toLowerCase();
        return Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1);
    }

    private static Map<String, Rol> cuentasDemo() {
        Map<String, Rol> cuentas = new LinkedHashMap<>();
        cuentas.put("sujeto@ejemplo.test", Rol.SUJETO);
        cuentas.put("academico@ejemplo.test", Rol.ACADEMICO);
        cuentas.put("psicologo@ejemplo.test", Rol.PSICOLOGO);
        cuentas.put("coordinador@ejemplo.test", Rol.COORDINADOR);
        cuentas.put("decano@ejemplo.test", Rol.DECANO);
        return Collections.unmodifiableMap(cuentas);
    }
}
