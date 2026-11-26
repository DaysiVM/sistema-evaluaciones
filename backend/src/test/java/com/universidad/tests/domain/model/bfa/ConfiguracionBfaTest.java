package com.universidad.tests.domain.model.bfa;

import static com.universidad.tests.domain.model.bfa.ConfiguracionBfaFixture.VERSION_ID;
import static com.universidad.tests.domain.model.bfa.ConfiguracionBfaFixture.catalogoOficial;
import static com.universidad.tests.domain.model.bfa.ConfiguracionBfaFixture.config;
import static com.universidad.tests.domain.model.bfa.ConfiguracionBfaFixture.configuracionOficial;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.universidad.tests.domain.exception.bfa.ConfiguracionBfaInvalidaException;
import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.TipoRespuesta;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConfiguracionBfaTest {

    @Test
    void perfilOficialTiene13SubpruebasYDuracionTotalDe5070Segundos() {
        assertEquals(13, SubpruebaBfa.values().length);
        ConfiguracionBfa cfg =
                ConfiguracionBfa.construir(VERSION_ID, catalogoOficial(), configuracionOficial());
        assertEquals(13, cfg.getSubpruebas().size());
        assertEquals(5070, cfg.tiempoTotalSegundos());
        assertEquals(SubpruebaBfa.VOC1, cfg.getSubpruebas().get(0).subprueba());
        assertEquals(SubpruebaBfa.FV, cfg.getSubpruebas().get(12).subprueba());
    }

    @Test
    void soloFluidezVerbalUsaTextoLibreConMaximoDe75() {
        for (SubpruebaBfa s : SubpruebaBfa.values()) {
            boolean fv = s == SubpruebaBfa.FV;
            assertEquals(fv ? Integer.valueOf(75) : null, s.getMaximoEntradas());
        }
    }

    @Test
    void faltaUnaSubpruebaSeReporta() {
        List<ConfiguracionSubprueba> sinCv = configuracionOficial();
        sinCv.removeIf(c -> c.getIdSubprueba() == 100L + SubpruebaBfa.CV.getOrden());
        ConfiguracionBfaInvalidaException ex =
                assertThrows(
                        ConfiguracionBfaInvalidaException.class,
                        () -> ConfiguracionBfa.construir(VERSION_ID, catalogoOficial(), sinCv));
        assertTrue(ex.getDiscrepancias().get(0).contains("CV"));
    }

    @Test
    void tiempoOrdenOMaximoDistintosSeAcumulan() {
        List<ConfiguracionSubprueba> cfgs = configuracionOficial();
        cfgs.set(0, config(SubpruebaBfa.VOC1, 2, 999, null));
        cfgs.set(12, config(SubpruebaBfa.FV, 13, 300, 50));
        ConfiguracionBfaInvalidaException ex =
                assertThrows(
                        ConfiguracionBfaInvalidaException.class,
                        () -> ConfiguracionBfa.construir(VERSION_ID, catalogoOficial(), cfgs));
        assertEquals(3, ex.getDiscrepancias().size());
    }

    @Test
    void subpruebaNoOficialSeRechaza() {
        List<Subprueba> catalogo = catalogoOficial();
        catalogo.add(
                new Subprueba(999L, ConfiguracionBfaFixture.TEST_ID, "XX", "Extra", null, null));
        List<ConfiguracionSubprueba> cfgs = configuracionOficial();
        cfgs.add(
                new ConfiguracionSubprueba(
                        998L,
                        VERSION_ID,
                        999L,
                        14,
                        60,
                        TipoRespuesta.SELECCION,
                        "instrucciones/XX",
                        null,
                        null));
        ConfiguracionBfaInvalidaException ex =
                assertThrows(
                        ConfiguracionBfaInvalidaException.class,
                        () -> ConfiguracionBfa.construir(VERSION_ID, catalogo, cfgs));
        assertTrue(ex.getDiscrepancias().get(0).contains("XX"));
    }
}
