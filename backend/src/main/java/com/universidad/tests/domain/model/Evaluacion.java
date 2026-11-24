package com.universidad.tests.domain.model;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Aplicación individual de una versión de Test a un Sujeto en un Periodo (ENT-COM-05). La versión
 * queda fija desde el inicio y recuperar la evaluación conserva el mismo ID.
 */
public class Evaluacion {

    private final Long idEvaluacion;
    private final Long idSujeto;
    private final Long idVersionFormulario;
    private final Long idTest;
    private final Long idPeriodo;
    private final EstadoEvaluacion estado;
    private final Instant iniciadaEn;
    private final Instant finalizadaEn;
    private final List<EvaluacionSubprueba> subpruebas;

    private Evaluacion(
            Long idEvaluacion,
            Long idSujeto,
            Long idVersionFormulario,
            Long idTest,
            Long idPeriodo,
            EstadoEvaluacion estado,
            Instant iniciadaEn,
            Instant finalizadaEn,
            List<EvaluacionSubprueba> subpruebas) {

        exigir(idSujeto, "El sujeto es obligatorio");
        exigir(idVersionFormulario, "La versión del formulario es obligatoria");
        exigir(idTest, "El test es obligatorio");
        exigir(idPeriodo, "El período es obligatorio");
        exigir(estado, "El estado es obligatorio");
        exigir(iniciadaEn, "La fecha de inicio es obligatoria");

        if ((estado == EstadoEvaluacion.FINALIZADA) != (finalizadaEn != null)) {
            throw new IllegalArgumentException("Solo una evaluación FINALIZADA tiene fecha de fin");
        }
        if (finalizadaEn != null && finalizadaEn.isBefore(iniciadaEn)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior al inicio");
        }
        if (subpruebas == null || subpruebas.isEmpty()) {
            throw new IllegalArgumentException("La evaluación debe incluir al menos una subprueba");
        }

        Set<Long> configuraciones = new HashSet<>();
        for (EvaluacionSubprueba subprueba : subpruebas) {
            if (!configuraciones.add(subprueba.getIdConfiguracionSubprueba())) {
                throw new IllegalArgumentException("Una configuración no puede repetirse");
            }
        }

        this.idEvaluacion = idEvaluacion;
        this.idSujeto = idSujeto;
        this.idVersionFormulario = idVersionFormulario;
        this.idTest = idTest;
        this.idPeriodo = idPeriodo;
        this.estado = estado;
        this.iniciadaEn = iniciadaEn;
        this.finalizadaEn = finalizadaEn;
        this.subpruebas =
                subpruebas.stream()
                        .sorted(Comparator.comparingInt(EvaluacionSubprueba::getOrdenAplicacion))
                        .toList();
    }

    /** Crea una evaluación EN_CURSO con todas sus subpruebas PENDIENTES (E3). */
    public static Evaluacion iniciar(
            Long idSujeto,
            Long idVersionFormulario,
            Long idTest,
            Long idPeriodo,
            List<EvaluacionSubprueba> subpruebas,
            Instant ahora) {

        if (subpruebas != null
                && subpruebas.stream()
                        .anyMatch(s -> s.getEstado() != EstadoEvaluacionSubprueba.PENDIENTE)) {
            throw new IllegalArgumentException(
                    "Una evaluación nueva solo tiene subpruebas PENDIENTES");
        }
        return new Evaluacion(
                null,
                idSujeto,
                idVersionFormulario,
                idTest,
                idPeriodo,
                EstadoEvaluacion.EN_CURSO,
                ahora,
                null,
                subpruebas);
    }

    /** Reconstruye una evaluación guardada, sin cambiar su estado ni sus tiempos (E4). */
    public static Evaluacion restaurar(
            Long idEvaluacion,
            Long idSujeto,
            Long idVersionFormulario,
            Long idTest,
            Long idPeriodo,
            EstadoEvaluacion estado,
            Instant iniciadaEn,
            Instant finalizadaEn,
            List<EvaluacionSubprueba> subpruebas) {
        exigir(idEvaluacion, "El ID de la evaluación es obligatorio");
        return new Evaluacion(
                idEvaluacion,
                idSujeto,
                idVersionFormulario,
                idTest,
                idPeriodo,
                estado,
                iniciadaEn,
                finalizadaEn,
                subpruebas);
    }

    public Optional<EvaluacionSubprueba> subpruebaActiva() {
        return subpruebas.stream().filter(EvaluacionSubprueba::estaActiva).findFirst();
    }

    public Optional<EvaluacionSubprueba> buscarSubprueba(String codigo) {
        return subpruebas.stream().filter(s -> s.getCodigo().equals(codigo)).findFirst();
    }

    private static void exigir(Object valor, String mensaje) {
        if (valor == null) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public Long getIdSujeto() {
        return idSujeto;
    }

    public Long getIdVersionFormulario() {
        return idVersionFormulario;
    }

    public Long getIdTest() {
        return idTest;
    }

    public Long getIdPeriodo() {
        return idPeriodo;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }

    public Instant getIniciadaEn() {
        return iniciadaEn;
    }

    public Instant getFinalizadaEn() {
        return finalizadaEn;
    }

    public List<EvaluacionSubprueba> getSubpruebas() {
        return subpruebas;
    }
}
