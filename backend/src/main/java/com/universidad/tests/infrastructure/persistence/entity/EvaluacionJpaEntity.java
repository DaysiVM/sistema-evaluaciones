package com.universidad.tests.infrastructure.persistence.entity;

import com.universidad.tests.domain.model.EstadoEvaluacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "evaluacion")
public class EvaluacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    // Sin FK hasta GEN-35-BE (tablas sujeto y periodo)
    @Column(name = "id_sujeto", nullable = false)
    private Long idSujeto;

    @Column(name = "id_periodo", nullable = false)
    private Long idPeriodo;

    @Column(name = "id_test", nullable = false)
    private Long idTest;

    @Column(name = "id_version_formulario", nullable = false)
    private Long idVersionFormulario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEvaluacion estado;

    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @Column(name = "fecha_fin")
    private Instant fechaFin;

    protected EvaluacionJpaEntity() {
        // Constructor requerido por JPA
    }

    public EvaluacionJpaEntity(
            Long idSujeto,
            Long idPeriodo,
            Long idTest,
            Long idVersionFormulario,
            EstadoEvaluacion estado,
            Instant fechaInicio,
            Instant fechaFin) {
        this.idSujeto = idSujeto;
        this.idPeriodo = idPeriodo;
        this.idTest = idTest;
        this.idVersionFormulario = idVersionFormulario;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
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

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public Instant getFechaFin() {
        return fechaFin;
    }
}
