package com.universidad.tests.domain.model.bfa;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.Subprueba;
import java.util.ArrayList;
import java.util.List;

/** Datos de apoyo compartidos por las pruebas del modulo BFA. */
public final class ConfiguracionBfaFixture {

    public static final long TEST_ID = 1L;
    public static final long VERSION_ID = 10L;

    private ConfiguracionBfaFixture() {}

    public static List<Subprueba> catalogoOficial() {
        List<Subprueba> lista = new ArrayList<>();
        for (SubpruebaBfa s : SubpruebaBfa.values()) {
            lista.add(
                    new Subprueba(
                            100L + s.getOrden(),
                            TEST_ID,
                            s.getCodigo(),
                            s.getNombre(),
                            null,
                            s.getFactor()));
        }
        return lista;
    }

    public static ConfiguracionSubprueba config(
            SubpruebaBfa s, int orden, Integer tiempo, Integer maxEntradas) {
        return new ConfiguracionSubprueba(
                200L + s.getOrden(),
                VERSION_ID,
                100L + s.getOrden(),
                orden,
                tiempo,
                s.getTipoRespuesta(),
                "instrucciones/" + s.getCodigo(),
                null,
                maxEntradas);
    }

    public static List<ConfiguracionSubprueba> configuracionOficial() {
        List<ConfiguracionSubprueba> lista = new ArrayList<>();
        for (SubpruebaBfa s : SubpruebaBfa.values()) {
            lista.add(config(s, s.getOrden(), s.getTiempoLimiteSegundos(), s.getMaximoEntradas()));
        }
        return lista;
    }
}
