package com.universidad.tests.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "respuesta_evaluacion")
public class RespuestaEvaluacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta_evaluacion")
    private Long idRespuestaEvaluacion;

    @Column(name = "id_evaluacion", nullable = false)
    private Long idEvaluacion;

    @Column(name = "subprueba_codigo", nullable = false, length = 80)
    private String subpruebaCodigo;

    @Column(name = "id_pregunta")
    private Long idPregunta;

    @Column(name = "id_opcion")
    private Long idOpcion;

    @Column(name = "valor_texto", columnDefinition = "text")
    private String valorTexto;

    @Column(name = "orden_entrada", nullable = false)
    private int ordenEntrada;

    @Column(name = "fecha_registro", nullable = false)
    private Instant fechaRegistro;

    protected RespuestaEvaluacionJpaEntity() {
        // Constructor requerido por JPA
    }

    public RespuestaEvaluacionJpaEntity(
            Long idEvaluacion,
            String subpruebaCodigo,
            Long idPregunta,
            Long idOpcion,
            String valorTexto,
            int ordenEntrada,
            Instant fechaRegistro) {
        this.idEvaluacion = idEvaluacion;
        this.subpruebaCodigo = subpruebaCodigo;
        this.idPregunta = idPregunta;
        this.idOpcion = idOpcion;
        this.valorTexto = valorTexto;
        this.ordenEntrada = ordenEntrada;
        this.fechaRegistro = fechaRegistro;
    }

    public void actualizar(Long opcion, String texto, Instant registro) {
        if (!Objects.equals(idOpcion, opcion) || !Objects.equals(valorTexto, texto)) {
            idOpcion = opcion;
            valorTexto = texto;
            fechaRegistro = registro;
        }
    }

    public Long getIdRespuestaEvaluacion() {
        return idRespuestaEvaluacion;
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public String getSubpruebaCodigo() {
        return subpruebaCodigo;
    }

    public Long getIdPregunta() {
        return idPregunta;
    }

    public Long getIdOpcion() {
        return idOpcion;
    }

    public String getValorTexto() {
        return valorTexto;
    }

    public int getOrdenEntrada() {
        return ordenEntrada;
    }

    public Instant getFechaRegistro() {
        return fechaRegistro;
    }
}
