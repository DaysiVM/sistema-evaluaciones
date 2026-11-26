package com.universidad.tests.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "solicitud_calculo_subprueba")
public class SolicitudCalculoSubpruebaJpaEntity {

    @Id
    @Column(name = "id_evaluacion_subprueba")
    private Long idEvaluacionSubprueba;

    @Column(name = "id_evaluacion", nullable = false)
    private Long idEvaluacion;

    @Column(name = "codigo_test", nullable = false, length = 80)
    private String codigoTest;

    @Column(name = "codigo_subprueba", nullable = false, length = 80)
    private String codigoSubprueba;

    @Column(name = "fecha_cierre", nullable = false)
    private Instant fechaCierre;

    @Column(name = "procesada_en")
    private Instant procesadaEn;

    protected SolicitudCalculoSubpruebaJpaEntity() {
        // Constructor requerido por JPA
    }

    public SolicitudCalculoSubpruebaJpaEntity(
            Long idEvaluacionSubprueba,
            Long idEvaluacion,
            String codigoTest,
            String codigoSubprueba,
            Instant fechaCierre,
            Instant procesadaEn) {
        this.idEvaluacionSubprueba = idEvaluacionSubprueba;
        this.idEvaluacion = idEvaluacion;
        this.codigoTest = codigoTest;
        this.codigoSubprueba = codigoSubprueba;
        this.fechaCierre = fechaCierre;
        this.procesadaEn = procesadaEn;
    }

    public Long getIdEvaluacionSubprueba() {
        return idEvaluacionSubprueba;
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public String getCodigoTest() {
        return codigoTest;
    }

    public String getCodigoSubprueba() {
        return codigoSubprueba;
    }

    public Instant getFechaCierre() {
        return fechaCierre;
    }

    public Instant getProcesadaEn() {
        return procesadaEn;
    }
}
