package com.universidad.tests.domain.model;

import java.time.Duration;
import java.time.Instant;

// Subprueba dentro de una evaluacion: PENDIENTE -> ACTIVA -> CERRADA
public record EvaluacionSubprueba(
        Long idEvaluacionSubprueba,
        Long idEvaluacion,
        Long idConfiguracionSubprueba,
        String codigo,
        String nombre,
        int orden,
        int duracionSegundos,
        EstadoSubprueba estado,
        String instrucciones,
        boolean permitirRetroceso,
        Instant fechaInicio,
        Instant fechaFinLimite,
        Instant fechaCierre,
        String letraInicial,
        int maximoEntradas) {

    public static EvaluacionSubprueba pendiente(
            Long idConfiguracionSubprueba,
            String codigo,
            String nombre,
            int orden,
            int duracionSegundos,
            String instrucciones,
            boolean permitirRetroceso,
            String letraInicial,
            int maximoEntradas) {
        return new EvaluacionSubprueba(
                null,
                null,
                idConfiguracionSubprueba,
                codigo,
                nombre,
                orden,
                duracionSegundos,
                EstadoSubprueba.PENDIENTE,
                instrucciones,
                permitirRetroceso,
                null,
                null,
                null,
                letraInicial,
                maximoEntradas);
    }

    public static EvaluacionSubprueba pendiente(
            Long idConfiguracionSubprueba, String codigo, int ordenAplicacion) {
        return pendiente(
                idConfiguracionSubprueba, codigo, codigo, ordenAplicacion, 0, null, true, null, 75);
    }

    public static EvaluacionSubprueba restaurar(
            Long idEvaluacionSubprueba,
            Long idConfiguracionSubprueba,
            String codigo,
            int ordenAplicacion,
            EstadoSubprueba estado,
            Instant iniciadaEn,
            Instant finLimite,
            Instant cerradaEn) {
        return new EvaluacionSubprueba(
                idEvaluacionSubprueba,
                null,
                idConfiguracionSubprueba,
                codigo,
                codigo,
                ordenAplicacion,
                0,
                estado,
                null,
                true,
                iniciadaEn,
                finLimite,
                cerradaEn,
                null,
                75);
    }

    // El tiempo restante lo calcula siempre el servidor; solo existe mientras esta ACTIVA
    public Long segundosRestantes(Instant ahora) {
        if (estado != EstadoSubprueba.ACTIVA || fechaFinLimite == null) {
            return null;
        }
        return Math.max(0, Duration.between(ahora, fechaFinLimite).getSeconds());
    }

    public Long getIdEvaluacionSubprueba() {
        return idEvaluacionSubprueba;
    }

    public Long getIdConfiguracionSubprueba() {
        return idConfiguracionSubprueba;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getOrden() {
        return orden;
    }

    public int getOrdenAplicacion() {
        return orden;
    }

    public EstadoSubprueba getEstado() {
        return estado;
    }

    public boolean estaActiva() {
        return estado == EstadoSubprueba.ACTIVA;
    }

    public Instant getFinLimite() {
        return fechaFinLimite;
    }

    public boolean esFluidezVerbal() {
        return "FV".equalsIgnoreCase(codigo);
    }

    public EvaluacionSubprueba iniciar(Instant ahora) {
        return new EvaluacionSubprueba(
                idEvaluacionSubprueba,
                idEvaluacion,
                idConfiguracionSubprueba,
                codigo,
                nombre,
                orden,
                duracionSegundos,
                EstadoSubprueba.ACTIVA,
                instrucciones,
                permitirRetroceso,
                ahora,
                ahora.plusSeconds(duracionSegundos),
                null,
                letraInicial,
                maximoEntradas);
    }

    public boolean vencio(Instant ahora) {
        return fechaFinLimite != null && ahora.isAfter(fechaFinLimite);
    }
}
