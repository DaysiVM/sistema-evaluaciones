package com.universidad.tests.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "opcion_respuesta")
public class OpcionRespuestaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_opcion")
    private Long idOpcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pregunta", nullable = false)
    private PreguntaJpaEntity pregunta;

    @Column(name = "codigo", nullable = false, length = 40)
    private String codigo;

    @Column(name = "orden_presentacion", nullable = false)
    private int ordenPresentacion;

    @Column(name = "texto", columnDefinition = "text")
    private String texto;

    @Column(name = "recurso_ref", columnDefinition = "text")
    private String recursoRef;

    protected OpcionRespuestaJpaEntity() {
        // Constructor requerido por JPA
    }

    public OpcionRespuestaJpaEntity(
            PreguntaJpaEntity pregunta,
            String codigo,
            int ordenPresentacion,
            String texto,
            String recursoRef) {

        if (pregunta == null) {
            throw new IllegalArgumentException("La opcion debe pertenecer a una pregunta");
        }

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo de la opcion es obligatorio");
        }

        if (ordenPresentacion <= 0) {
            throw new IllegalArgumentException("El orden de presentacion debe ser positivo");
        }

        if (texto != null && texto.isBlank()) {
            throw new IllegalArgumentException("El texto de la opcion no puede estar vacio");
        }

        if (recursoRef != null && recursoRef.isBlank()) {
            throw new IllegalArgumentException("La referencia del recurso no puede estar vacia");
        }

        if (texto == null && recursoRef == null) {
            throw new IllegalArgumentException("La opcion necesita texto o un recurso");
        }

        this.pregunta = pregunta;
        this.codigo = codigo;
        this.ordenPresentacion = ordenPresentacion;
        this.texto = texto;
        this.recursoRef = recursoRef;
    }

    public Long getIdOpcion() {
        return idOpcion;
    }

    public PreguntaJpaEntity getPregunta() {
        return pregunta;
    }

    public Long getIdPregunta() {
        return pregunta.getIdPregunta();
    }

    public String getCodigo() {
        return codigo;
    }

    public int getOrdenPresentacion() {
        return ordenPresentacion;
    }

    public String getTexto() {
        return texto;
    }

    public String getRecursoRef() {
        return recursoRef;
    }

    public boolean tieneRecurso() {
        return recursoRef != null;
    }

    public void actualizarDatos(
            String codigo, int ordenPresentacion, String texto, String recursoRef) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo de la opcion es obligatorio");
        }

        if (ordenPresentacion <= 0) {
            throw new IllegalArgumentException("El orden de presentacion debe ser positivo");
        }

        boolean sinTexto = texto == null || texto.isBlank();

        boolean sinRecurso = recursoRef == null || recursoRef.isBlank();

        if (sinTexto && sinRecurso) {
            throw new IllegalArgumentException("La opcion necesita texto o un recurso");
        }

        this.codigo = codigo;
        this.ordenPresentacion = ordenPresentacion;
        this.texto = texto;
        this.recursoRef = recursoRef;
    }
}
