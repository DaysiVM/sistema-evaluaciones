package com.universidad.tests.domain.model;

import java.time.Duration;
import java.time.Instant;

/**
 * Ejecución de una configuración dentro de una evaluación (ENT-COM-06). El código y el orden se
 * leen de la configuración de la versión; no son copias editables.
 */
public class EvaluacionSubprueba {

    private final Long idEvaluacionSubprueba;
    private final Long idConfiguracionSubprueba;
    private final String codigo;
    private final int ordenAplicacion;
    private final EstadoEvaluacionSubprueba estado;
    private final Instant iniciadaEn;
    private final Instant finLimite;
    private final Instant cerradaEn;

    private EvaluacionSubprueba(
            Long idEvaluacionSubprueba,
            Long idConfiguracionSubprueba,
            String codigo,
            int ordenAplicacion,
            EstadoEvaluacionSubprueba estado,
            Instant iniciadaEn,
            Instant finLimite,
            Instant cerradaEn) {

        if (idConfiguracionSubprueba == null) {
            throw new IllegalArgumentException("La configuración de la subprueba es obligatoria");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la subprueba es obligatorio");
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la subprueba es obligatorio");
        }
        if (estado == EstadoEvaluacionSubprueba.PENDIENTE
                && (iniciadaEn != null || finLimite != null || cerradaEn != null)) {
            throw new IllegalArgumentException("Una subprueba PENDIENTE no tiene tiempos");
        }
        if (estado != EstadoEvaluacionSubprueba.PENDIENTE && iniciadaEn == null) {
            throw new IllegalArgumentException("Una subprueba iniciada debe tener inicio");
        }
        if ((estado == EstadoEvaluacionSubprueba.CERRADA) != (cerradaEn != null)) {
            throw new IllegalArgumentException("Solo una subprueba CERRADA tiene cierre");
        }

        this.idEvaluacionSubprueba = idEvaluacionSubprueba;
        this.idConfiguracionSubprueba = idConfiguracionSubprueba;
        this.codigo = codigo;
        this.ordenAplicacion = ordenAplicacion;
        this.estado = estado;
        this.iniciadaEn = iniciadaEn;
        this.finLimite = finLimite;
        this.cerradaEn = cerradaEn;
    }

    /** Nueva ejecución al crear la evaluación: queda PENDIENTE y sin tiempos. */
    public static EvaluacionSubprueba pendiente(
            Long idConfiguracionSubprueba, String codigo, int ordenAplicacion) {
        return new EvaluacionSubprueba(
                null,
                idConfiguracionSubprueba,
                codigo,
                ordenAplicacion,
                EstadoEvaluacionSubprueba.PENDIENTE,
                null,
                null,
                null);
    }

    /** Reconstruye una ejecución guardada. */
    public static EvaluacionSubprueba restaurar(
            Long idEvaluacionSubprueba,
            Long idConfiguracionSubprueba,
            String codigo,
            int ordenAplicacion,
            EstadoEvaluacionSubprueba estado,
            Instant iniciadaEn,
            Instant finLimite,
            Instant cerradaEn) {
        return new EvaluacionSubprueba(
                idEvaluacionSubprueba,
                idConfiguracionSubprueba,
                codigo,
                ordenAplicacion,
                estado,
                iniciadaEn,
                finLimite,
                cerradaEn);
    }

    public boolean estaActiva() {
        return estado == EstadoEvaluacionSubprueba.ACTIVA;
    }

    /**
     * Tiempo restante derivado: max(0, finLimite − horaServidor). Nulo si la subprueba no está
     * activa o no es cronometrada. Consultarlo nunca modifica el reloj.
     */
    public Long segundosRestantes(Instant ahora) {
        if (!estaActiva() || finLimite == null) {
            return null;
        }
        long segundos = Duration.between(ahora, finLimite).getSeconds();
        return Math.max(0L, segundos);
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

    public int getOrdenAplicacion() {
        return ordenAplicacion;
    }

    public EstadoEvaluacionSubprueba getEstado() {
        return estado;
    }

    public Instant getIniciadaEn() {
        return iniciadaEn;
    }

    public Instant getFinLimite() {
        return finLimite;
    }

    public Instant getCerradaEn() {
        return cerradaEn;
    }
}
