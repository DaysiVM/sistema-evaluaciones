package com.universidad.tests.infrastructure.persistence.bfa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aplicacion_fluidez_bfa")
public class AplicacionFluidezBfaJpaEntity {

    @Id
    @Column(name = "id_evaluacion_subprueba")
    private Long idEvaluacionSubprueba;

    @Column(name = "letra_inicial", nullable = false, length = 1)
    private String letraInicial;

    protected AplicacionFluidezBfaJpaEntity() {
        // Constructor requerido por JPA
    }

    public AplicacionFluidezBfaJpaEntity(Long idEvaluacionSubprueba, String letraInicial) {
        this.idEvaluacionSubprueba = idEvaluacionSubprueba;
        this.letraInicial = letraInicial;
    }

    public Long getIdEvaluacionSubprueba() {
        return idEvaluacionSubprueba;
    }

    public String getLetraInicial() {
        return letraInicial;
    }
}
