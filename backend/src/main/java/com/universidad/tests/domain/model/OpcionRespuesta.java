package com.universidad.tests.domain.model;

public class OpcionRespuesta {

    private final Long idOpcion;
    private final Long idPregunta;
    private final String codigo;
    private final int ordenPresentacion;
    private final String texto;
    private final String recursoRef;

    public OpcionRespuesta(
            Long idOpcion,
            Long idPregunta,
            String codigo,
            int ordenPresentacion,
            String texto,
            String recursoRef) {

        if (idPregunta == null) {
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
            throw new IllegalArgumentException("La opcion necesita texto o recurso");
        }

        this.idOpcion = idOpcion;
        this.idPregunta = idPregunta;
        this.codigo = codigo;
        this.ordenPresentacion = ordenPresentacion;
        this.texto = texto;
        this.recursoRef = recursoRef;
    }

    public Long getIdOpcion() {
        return idOpcion;
    }

    public Long getIdPregunta() {
        return idPregunta;
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
}
