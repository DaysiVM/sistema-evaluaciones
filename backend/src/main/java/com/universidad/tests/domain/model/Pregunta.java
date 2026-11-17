package com.universidad.tests.domain.model;

public class Pregunta {

    private final Long idPregunta;
    private final Long idConfiguracionSubprueba;
    private final String numeroVisible;
    private final int ordenPresentacion;
    private final String enunciado;
    private final String recursoRef;

    public Pregunta(
            Long idPregunta,
            Long idConfiguracionSubprueba,
            String numeroVisible,
            int ordenPresentacion,
            String enunciado,
            String recursoRef) {

        if (idConfiguracionSubprueba == null) {
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

        this.idPregunta = idPregunta;
        this.idConfiguracionSubprueba = idConfiguracionSubprueba;
        this.numeroVisible = numeroVisible;
        this.ordenPresentacion = ordenPresentacion;
        this.enunciado = enunciado;
        this.recursoRef = recursoRef;
    }

    public Long getIdPregunta() {
        return idPregunta;
    }

    public Long getIdConfiguracionSubprueba() {
        return idConfiguracionSubprueba;
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
}
