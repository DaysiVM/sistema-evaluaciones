package com.universidad.tests.config.bfa;

import com.universidad.tests.application.usecase.bfa.CargarCatalogoDemoBfaUseCase;
import com.universidad.tests.domain.model.VersionFormulario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Carga los datos ficticios del BFA al arrancar con el perfil demo. Nunca detiene el arranque: si
 * algo falla lo registra y sigue, y si no puede publicar deja la version en BORRADOR.
 */
@Component
@Profile("demo")
public class CargadorDemoBfaRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CargadorDemoBfaRunner.class);

    private final CargarCatalogoDemoBfaUseCase cargador;

    public CargadorDemoBfaRunner(CargarCatalogoDemoBfaUseCase cargador) {
        this.cargador = cargador;
    }

    @Override
    public void run(ApplicationArguments args) {
        VersionFormulario version;
        try {
            version = cargador.cargarContenido();
        } catch (RuntimeException e) {
            LOG.warn("No se pudo cargar el contenido demo del BFA; el arranque continua", e);
            return;
        }
        try {
            VersionFormulario resultado = cargador.publicar(version.getIdVersionFormulario());
            LOG.info(
                    "Contenido demo del BFA: version {} en estado {}",
                    resultado.getCodigoVersion(),
                    resultado.getEstado());
        } catch (RuntimeException e) {
            LOG.warn(
                    "No se pudo publicar la version demo del BFA; queda en BORRADOR."
                            + " El arranque continua",
                    e);
        }
    }
}
