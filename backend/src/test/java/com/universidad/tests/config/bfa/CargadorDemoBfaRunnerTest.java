package com.universidad.tests.config.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.universidad.tests.application.usecase.bfa.CargarCatalogoDemoBfaUseCase;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.VersionFormulario;
import org.junit.jupiter.api.Test;

class CargadorDemoBfaRunnerTest {

    private static VersionFormulario borrador() {
        return VersionFormulario.restaurar(
                7L,
                1L,
                "DEMO-1",
                "Version demo",
                "demo/datos-ficticios",
                EstadoVersionFormulario.BORRADOR,
                null,
                null);
    }

    /** Cargador de prueba: cuenta llamadas y falla donde se le pida. */
    private static final class CargadorFalso extends CargarCatalogoDemoBfaUseCase {
        private final boolean falloCarga;
        private final boolean falloPublicacion;
        int cargas;
        int publicaciones;

        CargadorFalso(boolean falloCarga, boolean falloPublicacion) {
            super(null, null, null, null, null, null, null);
            this.falloCarga = falloCarga;
            this.falloPublicacion = falloPublicacion;
        }

        @Override
        public VersionFormulario cargarContenido() {
            cargas++;
            if (falloCarga) {
                throw new IllegalStateException("fallo de carga simulado");
            }
            return borrador();
        }

        @Override
        public VersionFormulario publicar(Long idVersionFormulario) {
            publicaciones++;
            if (falloPublicacion) {
                throw new IllegalStateException("fallo de publicacion simulado");
            }
            return borrador();
        }
    }

    @Test
    void cargaYPublicaCuandoTodoSaleBien() {
        CargadorFalso cargador = new CargadorFalso(false, false);

        new CargadorDemoBfaRunner(cargador).run(null);

        assertEquals(1, cargador.cargas);
        assertEquals(1, cargador.publicaciones);
    }

    @Test
    void siFallaLaCargaNoPropagaNiIntentaPublicar() {
        CargadorFalso cargador = new CargadorFalso(true, false);

        new CargadorDemoBfaRunner(cargador).run(null);

        assertEquals(1, cargador.cargas);
        assertEquals(0, cargador.publicaciones);
    }

    @Test
    void siFallaLaPublicacionNoPropagaYElArranqueContinua() {
        CargadorFalso cargador = new CargadorFalso(false, true);

        new CargadorDemoBfaRunner(cargador).run(null);

        assertEquals(1, cargador.publicaciones);
    }
}
