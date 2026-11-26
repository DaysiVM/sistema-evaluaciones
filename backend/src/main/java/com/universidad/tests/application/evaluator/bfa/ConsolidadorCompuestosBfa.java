package com.universidad.tests.application.evaluator.bfa;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Consolidates the BFA composite scores whose formulas are explicitly defined. */
public final class ConsolidadorCompuestosBfa {

    public static final String VOCT = "VOCT";
    public static final String ST = "ST";
    public static final String RT = "RT";
    public static final String NT = "NT";
    public static final String EIV = "EIV";
    public static final String EIC = "EIC";
    public static final String EIG = "EIG";
    public static final String AE = "AE";

    private static final String VOC1 = "VOC1";
    private static final String VOC2 = "VOC2";
    private static final String CV = "CV";
    private static final String S1 = "S1";
    private static final String S2 = "S2";
    private static final String R1 = "R1";
    private static final String R2 = "R2";
    private static final String N1 = "N1";
    private static final String N2 = "N2";
    private static final String FV = "FV";

    /**
     * Calculates each composite independently. A composite is pending if any of its inputs is
     * missing; missing scores are never treated as zero.
     *
     * @param puntuacionesBase scores keyed by their approved BFA factor codes
     * @return all eight composites, with null score and a reason for each pending composite
     */
    public Map<String, PuntuacionCompuesta> consolidar(Map<String, Integer> puntuacionesBase) {
        Objects.requireNonNull(puntuacionesBase, "puntuacionesBase es obligatorio");

        Map<String, PuntuacionCompuesta> resultado = new LinkedHashMap<>();
        agregar(resultado, VOCT, puntuacionesBase, VOC1, VOC2);
        agregar(resultado, ST, puntuacionesBase, S1, S2);
        agregar(resultado, RT, puntuacionesBase, R1, R2);
        agregar(resultado, NT, puntuacionesBase, N1, N2);
        Map<String, Object> entradasConCompuestos = new HashMap<>();
        entradasConCompuestos.putAll(puntuacionesBase);
        entradasConCompuestos.putAll(resultado);
        agregarPonderado(resultado, EIV, entradasConCompuestos, CV, 2, VOCT, 1, FV, 1);
        agregarPonderado(resultado, EIC, resultado, RT, 2, ST, 1, NT, 1);
        agregar(resultado, EIG, resultado, EIV, EIC);
        entradasConCompuestos.putAll(resultado);
        agregarPonderado(resultado, AE, entradasConCompuestos, RT, 2, CV, 1, VOCT, 1);
        return Map.copyOf(resultado);
    }

    /** N1 applies its floor only after both numeric blocks' hits and errors have been combined. */
    public int calcularN1(
            int aciertosBloqueA, int erroresBloqueA, int aciertosBloqueB, int erroresBloqueB) {
        if (aciertosBloqueA < 0
                || erroresBloqueA < 0
                || aciertosBloqueB < 0
                || erroresBloqueB < 0) {
            throw new IllegalArgumentException("Aciertos y errores deben ser no negativos");
        }
        int aciertos = Math.addExact(aciertosBloqueA, aciertosBloqueB);
        int errores = Math.addExact(erroresBloqueA, erroresBloqueB);
        return Math.max(0, Math.subtractExact(aciertos, errores));
    }

    private static void agregar(
            Map<String, PuntuacionCompuesta> destino,
            String codigo,
            Map<String, ?> entradas,
            String... codigosEntrada) {
        int total = 0;
        for (String codigoEntrada : codigosEntrada) {
            Integer puntuacion = puntuacion(entradas, codigoEntrada);
            if (puntuacion == null) {
                destino.put(codigo, PuntuacionCompuesta.pendiente(codigoEntrada));
                return;
            }
            total = Math.addExact(total, puntuacion);
        }
        destino.put(codigo, PuntuacionCompuesta.calculada(total));
    }

    private static void agregarPonderado(
            Map<String, PuntuacionCompuesta> destino,
            String codigo,
            Map<String, ?> entradas,
            String primerCodigo,
            int primerPeso,
            String segundoCodigo,
            int segundoPeso,
            String tercerCodigo,
            int tercerPeso) {
        Integer primero = puntuacion(entradas, primerCodigo);
        Integer segundo = puntuacion(entradas, segundoCodigo);
        Integer tercero = puntuacion(entradas, tercerCodigo);
        if (primero == null || segundo == null || tercero == null) {
            String faltante =
                    primero == null ? primerCodigo : segundo == null ? segundoCodigo : tercerCodigo;
            destino.put(codigo, PuntuacionCompuesta.pendiente(faltante));
            return;
        }
        int total =
                Math.addExact(
                        Math.addExact(
                                Math.multiplyExact(primerPeso, primero),
                                Math.multiplyExact(segundoPeso, segundo)),
                        Math.multiplyExact(tercerPeso, tercero));
        destino.put(codigo, PuntuacionCompuesta.calculada(total));
    }

    private static Integer puntuacion(Map<String, ?> entradas, String codigo) {
        Object valor = entradas.get(codigo);
        if (valor instanceof Integer puntuacion) {
            return puntuacion;
        }
        if (valor instanceof PuntuacionCompuesta compuesta
                && compuesta.estadoCalculo() == EstadoCalculo.CALCULADA) {
            return compuesta.puntuacionDirecta();
        }
        return null;
    }

    public record PuntuacionCompuesta(
            Integer puntuacionDirecta, EstadoCalculo estadoCalculo, String motivo) {
        public PuntuacionCompuesta {
            Objects.requireNonNull(estadoCalculo, "estadoCalculo es obligatorio");
            if (estadoCalculo == EstadoCalculo.CALCULADA && puntuacionDirecta == null) {
                throw new IllegalArgumentException("Una puntuacion calculada debe tener valor");
            }
            if (estadoCalculo == EstadoCalculo.PENDIENTE && puntuacionDirecta != null) {
                throw new IllegalArgumentException("Una puntuacion pendiente no puede tener valor");
            }
        }

        private static PuntuacionCompuesta calculada(int puntuacion) {
            return new PuntuacionCompuesta(puntuacion, EstadoCalculo.CALCULADA, null);
        }

        private static PuntuacionCompuesta pendiente(String codigoFaltante) {
            return new PuntuacionCompuesta(
                    null, EstadoCalculo.PENDIENTE, "FALTA_PUNTUACION_" + codigoFaltante);
        }
    }

    public enum EstadoCalculo {
        CALCULADA,
        PENDIENTE
    }
}
