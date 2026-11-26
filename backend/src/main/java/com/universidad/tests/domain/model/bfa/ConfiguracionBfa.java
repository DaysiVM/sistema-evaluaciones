package com.universidad.tests.domain.model.bfa;

import com.universidad.tests.domain.exception.bfa.ConfiguracionBfaInvalidaException;
import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.Subprueba;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Configuracion validada de una edicion del BFA: las 13 subpruebas oficiales, en orden. */
public final class ConfiguracionBfa {

    public static final String CODIGO_TEST = "BFA";

    private final Long idVersionFormulario;
    private final List<SubpruebaBfaConfigurada> subpruebas;

    private ConfiguracionBfa(Long idVersionFormulario, List<SubpruebaBfaConfigurada> subpruebas) {
        this.idVersionFormulario = idVersionFormulario;
        this.subpruebas = List.copyOf(subpruebas);
    }

    /**
     * Valida el catalogo contra el perfil oficial y construye la configuracion. Reune todas las
     * discrepancias en una sola excepcion.
     */
    public static ConfiguracionBfa construir(
            Long idVersionFormulario,
            List<Subprueba> subpruebasCatalogo,
            List<ConfiguracionSubprueba> configuraciones) {
        Objects.requireNonNull(idVersionFormulario, "idVersionFormulario");
        List<String> errores = new ArrayList<>();

        Map<Long, Subprueba> porId = new HashMap<>();
        Map<String, Subprueba> porCodigo = new HashMap<>();
        for (Subprueba s : subpruebasCatalogo) {
            porId.put(s.getIdSubprueba(), s);
            porCodigo.put(s.getCodigo(), s);
        }

        Map<String, ConfiguracionSubprueba> configPorCodigo = new HashMap<>();
        for (ConfiguracionSubprueba c : configuraciones) {
            Subprueba s = porId.get(c.getIdSubprueba());
            if (s == null) {
                errores.add(
                        "La configuracion referencia una subprueba ajena al BFA: "
                                + c.getIdSubprueba());
            } else if (configPorCodigo.put(s.getCodigo(), c) != null) {
                errores.add("Subprueba configurada mas de una vez: " + s.getCodigo());
            }
        }

        List<SubpruebaBfaConfigurada> resultado = new ArrayList<>();
        for (SubpruebaBfa oficial : SubpruebaBfa.values()) {
            Subprueba s = porCodigo.get(oficial.getCodigo());
            ConfiguracionSubprueba c = configPorCodigo.get(oficial.getCodigo());
            if (s == null) {
                errores.add("Falta la subprueba " + oficial.getCodigo() + " en el catalogo");
                continue;
            }
            if (c == null) {
                errores.add("Falta la configuracion de " + oficial.getCodigo() + " en la edicion");
                continue;
            }
            comparar(errores, oficial, c);
            resultado.add(
                    new SubpruebaBfaConfigurada(
                            oficial, s.getIdSubprueba(), c.getIdConfiguracionSubprueba()));
        }

        for (String codigo : configPorCodigo.keySet()) {
            if (java.util.Arrays.stream(SubpruebaBfa.values())
                    .noneMatch(o -> o.getCodigo().equals(codigo))) {
                errores.add("Subprueba no oficial en la edicion: " + codigo);
            }
        }

        if (!errores.isEmpty()) {
            throw new ConfiguracionBfaInvalidaException(errores);
        }
        return new ConfiguracionBfa(idVersionFormulario, resultado);
    }

    private static void comparar(
            List<String> errores, SubpruebaBfa oficial, ConfiguracionSubprueba c) {
        String cod = oficial.getCodigo();
        if (c.getOrdenAplicacion() != oficial.getOrden()) {
            errores.add(
                    cod
                            + ": orden "
                            + c.getOrdenAplicacion()
                            + " (esperado "
                            + oficial.getOrden()
                            + ")");
        }
        if (!Objects.equals(c.getTiempoLimiteSegundos(), oficial.getTiempoLimiteSegundos())) {
            errores.add(
                    cod
                            + ": tiempo "
                            + c.getTiempoLimiteSegundos()
                            + " (esperado "
                            + oficial.getTiempoLimiteSegundos()
                            + ")");
        }
        if (c.getTipoRespuesta() != oficial.getTipoRespuesta()) {
            errores.add(
                    cod
                            + ": tipo de respuesta "
                            + c.getTipoRespuesta()
                            + " (esperado "
                            + oficial.getTipoRespuesta()
                            + ")");
        }
        if (!Objects.equals(c.getMaximoEntradas(), oficial.getMaximoEntradas())) {
            errores.add(
                    cod
                            + ": maximoEntradas "
                            + c.getMaximoEntradas()
                            + " (esperado "
                            + oficial.getMaximoEntradas()
                            + ")");
        }
    }

    public Long getIdVersionFormulario() {
        return idVersionFormulario;
    }

    public List<SubpruebaBfaConfigurada> getSubpruebas() {
        return subpruebas;
    }

    public int tiempoTotalSegundos() {
        return subpruebas.stream().mapToInt(s -> s.subprueba().getTiempoLimiteSegundos()).sum();
    }
}
