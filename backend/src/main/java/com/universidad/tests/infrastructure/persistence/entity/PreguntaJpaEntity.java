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
@Table(name = "pregunta")
public class PreguntaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta")
    private Long idPregunta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_configuracion_subprueba", nullable = false)
    private ConfiguracionSubpruebaJpaEntity configuracionSubprueba;

    @Column(name = "numero_visible", nullable = false, length = 40)
    private String numeroVisible;

    @Column(name = "orden_presentacion", nullable = false)
    private int ordenPresentacion;

    @Column(name = "enunciado", columnDefinition = "text")
    private String enunciado;

    @Column(name = "recurso_ref", columnDefinition = "text")
    private String recursoRef;

    protected PreguntaJpaEntity() {
        // Constructor requerido por JPA
    }

    public PreguntaJpaEntity(
            ConfiguracionSubpruebaJpaEntity configuracionSubprueba,
            String numeroVisible,
            int ordenPresentacion,
            String enunciado,
            String recursoRef) {

        if (configuracionSubprueba == null) {
            throw new IllegalArgumentException("La pregunta debe pertenecer a una configuracion");
        }

        if (numeroVisible == null || numeroVisible.isBlank()) {
            throw new IllegalArgumentException("El numero visible es obligatorio");
        }

        if (ordenPresentacion <= 0) {
            throw new IllegalArgumentException("El orden de presentacion debe ser positivo");
        }

        if (enunciado != null && enunciado.isBlank()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacio");
        }

        if (recursoRef != null && recursoRef.isBlank()) {
            throw new IllegalArgumentException("La referencia del recurso no puede estar vacia");
        }

        if (enunciado == null && recursoRef == null) {
            throw new IllegalArgumentException("La pregunta necesita un enunciado o recurso");
        }

        this.configuracionSubprueba = configuracionSubprueba;
        this.numeroVisible = numeroVisible;
        this.ordenPresentacion = ordenPresentacion;
        this.enunciado = enunciado;
        this.recursoRef = recursoRef;
    }

    public Long getIdPregunta() {
        return idPregunta;
    }

    public ConfiguracionSubpruebaJpaEntity getConfiguracionSubprueba() {
        return configuracionSubprueba;
    }

    public Long getIdConfiguracionSubprueba() {
        return configuracionSubprueba.getIdConfiguracionSubprueba();
    }

    public String getNumeroVisible() {
        return numeroVisible;
    }

    public int getOrdenPresentacion() {
        return ordenPresentacion;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String getRecursoRef() {
        return recursoRef;
    }

    public boolean tieneRecurso() {
        return recursoRef != null;
    }

    public void actualizarDatos(
            String numeroVisible, int ordenPresentacion, String enunciado, String recursoRef) {

        if (numeroVisible == null || numeroVisible.isBlank()) {
            throw new IllegalArgumentException("El numero visible es obligatorio");
        }

        if (ordenPresentacion <= 0) {
            throw new IllegalArgumentException("El orden de presentacion debe ser positivo");
        }

        boolean sinEnunciado = enunciado == null || enunciado.isBlank();

        boolean sinRecurso = recursoRef == null || recursoRef.isBlank();

        if (sinEnunciado && sinRecurso) {
            throw new IllegalArgumentException("La pregunta necesita un enunciado o recurso");
        }

        this.numeroVisible = numeroVisible;
        this.ordenPresentacion = ordenPresentacion;
        this.enunciado = enunciado;
        this.recursoRef = recursoRef;
    }
}
