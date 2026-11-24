package com.universidad.tests.infrastructure.persistence.entity;

import com.universidad.tests.domain.model.EstadoEvaluacion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evaluacion")
public class EvaluacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    @Column(name = "id_sujeto", nullable = false)
    private Long idSujeto;

    @Column(name = "id_version_formulario", nullable = false)
    private Long idVersionFormulario;

    @Column(name = "id_test", nullable = false)
    private Long idTest;

    @Column(name = "id_periodo", nullable = false)
    private Long idPeriodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEvaluacion estado;

    @Column(name = "iniciada_en", nullable = false)
    private Instant iniciadaEn;

    @Column(name = "finalizada_en")
    private Instant finalizadaEn;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.PERSIST)
    private List<EvaluacionSubpruebaJpaEntity> subpruebas = new ArrayList<>();

    protected EvaluacionJpaEntity() {
        // Constructor requerido por JPA
    }

    public EvaluacionJpaEntity(
            Long idSujeto,
            Long idVersionFormulario,
            Long idTest,
            Long idPeriodo,
            EstadoEvaluacion estado,
            Instant iniciadaEn) {
        this.idSujeto = idSujeto;
        this.idVersionFormulario = idVersionFormulario;
        this.idTest = idTest;
        this.idPeriodo = idPeriodo;
        this.estado = estado;
        this.iniciadaEn = iniciadaEn;
    }

    public void agregarSubprueba(EvaluacionSubpruebaJpaEntity subprueba) {
        subpruebas.add(subprueba);
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

    public List<EvaluacionSubpruebaJpaEntity> getSubpruebas() {
        return subpruebas;
    }
}
