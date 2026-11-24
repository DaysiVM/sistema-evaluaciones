package com.universidad.tests.infrastructure.persistence.entity;

import com.universidad.tests.domain.model.EstadoEvaluacionSubprueba;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "evaluacion_subprueba")
public class EvaluacionSubpruebaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion_subprueba")
    private Long idEvaluacionSubprueba;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluacion", nullable = false)
    private EvaluacionJpaEntity evaluacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_configuracion_subprueba", nullable = false)
    private ConfiguracionSubpruebaJpaEntity configuracion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEvaluacionSubprueba estado;

    @Column(name = "iniciada_en")
    private Instant iniciadaEn;

    @Column(name = "fin_limite")
    private Instant finLimite;

    @Column(name = "cerrada_en")
    private Instant cerradaEn;

    protected EvaluacionSubpruebaJpaEntity() {
        // Constructor requerido por JPA
    }

    public EvaluacionSubpruebaJpaEntity(
            EvaluacionJpaEntity evaluacion, ConfiguracionSubpruebaJpaEntity configuracion) {
        this.evaluacion = evaluacion;
        this.configuracion = configuracion;
        this.estado = EstadoEvaluacionSubprueba.PENDIENTE;
    }

    public Long getIdEvaluacionSubprueba() {
        return idEvaluacionSubprueba;
    }

    public EvaluacionJpaEntity getEvaluacion() {
        return evaluacion;
    }

    public ConfiguracionSubpruebaJpaEntity getConfiguracion() {
        return configuracion;
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
