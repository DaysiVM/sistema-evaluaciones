package com.universidad.tests.domain.model;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record Evaluacion(
        Long idEvaluacion,
        Long idSujeto,
        Long idPeriodo,
        Long idTest,
        Long idVersionFormulario,
        EstadoEvaluacion estado,
        Instant fechaInicio,
        Instant fechaFin,
        List<EvaluacionSubprueba> subpruebas) {

    public Evaluacion {
        subpruebas = subpruebas == null ? List.of() : List.copyOf(subpruebas);
    }

    public static Evaluacion iniciar(
            Long idSujeto,
            Long idVersionFormulario,
            Long idTest,
            Long idPeriodo,
            List<EvaluacionSubprueba> subpruebas,
            Instant ahora) {
        if (subpruebas == null || subpruebas.isEmpty()) {
            throw new IllegalArgumentException("La evaluación debe incluir al menos una subprueba");
        }
        Set<Long> configuraciones = new HashSet<>();
        for (EvaluacionSubprueba subprueba : subpruebas) {
            if (subprueba.idConfiguracionSubprueba() != null
                    && !configuraciones.add(subprueba.idConfiguracionSubprueba())) {
                throw new IllegalArgumentException("Una configuración no puede repetirse");
            }
        }
        List<EvaluacionSubprueba> ordenadas =
                subpruebas.stream()
                        .sorted(Comparator.comparingInt(EvaluacionSubprueba::orden))
                        .toList();
        return new Evaluacion(
                null,
                idSujeto,
                idPeriodo,
                idTest,
                idVersionFormulario,
                EstadoEvaluacion.EN_CURSO,
                ahora,
                null,
                ordenadas);
    }

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
        if ((estado == EstadoEvaluacion.FINALIZADA) != (finalizadaEn != null)) {
            throw new IllegalArgumentException("Solo una evaluación FINALIZADA tiene fecha de fin");
        }
        if (finalizadaEn != null && finalizadaEn.isBefore(iniciadaEn)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior al inicio");
        }
        return new Evaluacion(
                idEvaluacion,
                idSujeto,
                idPeriodo,
                idTest,
                idVersionFormulario,
                estado,
                iniciadaEn,
                finalizadaEn,
                subpruebas);
    }

    public Optional<EvaluacionSubprueba> subpruebaActiva() {
        return subpruebas.stream().filter(s -> s.estado() == EstadoSubprueba.ACTIVA).findFirst();
    }

    public Optional<EvaluacionSubprueba> buscarSubprueba(String codigo) {
        return subpruebas.stream().filter(s -> s.codigo().equals(codigo)).findFirst();
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public Long getIdSujeto() {
        return idSujeto;
    }

    public Long getIdPeriodo() {
        return idPeriodo;
    }

    public Long getIdTest() {
        return idTest;
    }

    public Long getIdVersionFormulario() {
        return idVersionFormulario;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }

    public Instant getIniciadaEn() {
        return fechaInicio;
    }

    public Instant getFinalizadaEn() {
        return fechaFin;
    }

    public List<EvaluacionSubprueba> getSubpruebas() {
        return subpruebas;
    }
}
