package com.universidad.tests.domain.model;

public class ConfiguracionSubprueba {

    private final Long idConfiguracionSubprueba;
    private final Long idVersionFormulario;
    private final Long idSubprueba;
    private final int ordenAplicacion;
    private final Integer tiempoLimiteSegundos;
    private final TipoRespuesta tipoRespuesta;
    private final String instruccionesRef;
    private final String practicaRef;
    private final Integer maximoEntradas;

    public ConfiguracionSubprueba(
            Long idConfiguracionSubprueba,
            Long idVersionFormulario,
            Long idSubprueba,
            int ordenAplicacion,
            Integer tiempoLimiteSegundos,
            TipoRespuesta tipoRespuesta,
            String instruccionesRef,
            String practicaRef,
            Integer maximoEntradas) {

        if (idVersionFormulario == null || idSubprueba == null) {
            throw new IllegalArgumentException("La version y la subprueba son obligatorias");
        }

        if (ordenAplicacion <= 0) {
            throw new IllegalArgumentException("El orden de aplicacion debe ser positivo");
        }

        if (tiempoLimiteSegundos != null && tiempoLimiteSegundos <= 0) {
            throw new IllegalArgumentException("El tiempo limite debe ser positivo");
        }

        if (tipoRespuesta == null) {
            throw new IllegalArgumentException("El tipo de respuesta es obligatorio");
        }

        if (instruccionesRef == null || instruccionesRef.isBlank()) {
            throw new IllegalArgumentException("La referencia de instrucciones es obligatoria");
        }

        if (practicaRef != null && practicaRef.isBlank()) {
            throw new IllegalArgumentException("La referencia de practica no puede estar vacia");
        }

        if (tipoRespuesta == TipoRespuesta.SELECCION && maximoEntradas != null) {
            throw new IllegalArgumentException("La seleccion no utiliza maximoEntradas");
        }

        if (maximoEntradas != null && maximoEntradas <= 0) {
            throw new IllegalArgumentException("El maximo de entradas debe ser positivo");
        }

        this.idConfiguracionSubprueba = idConfiguracionSubprueba;
        this.idVersionFormulario = idVersionFormulario;
        this.idSubprueba = idSubprueba;
        this.ordenAplicacion = ordenAplicacion;
        this.tiempoLimiteSegundos = tiempoLimiteSegundos;
        this.tipoRespuesta = tipoRespuesta;
        this.instruccionesRef = instruccionesRef;
        this.practicaRef = practicaRef;
        this.maximoEntradas = maximoEntradas;
    }

    public boolean tieneTiempoLimite() {
        return tiempoLimiteSegundos != null;
    }

    public Long getIdConfiguracionSubprueba() {
        return idConfiguracionSubprueba;
    }

    public Long getIdVersionFormulario() {
        return idVersionFormulario;
    }

    public Long getIdSubprueba() {
        return idSubprueba;
    }

    public int getOrdenAplicacion() {
        return ordenAplicacion;
    }

    public Integer getTiempoLimiteSegundos() {
        return tiempoLimiteSegundos;
    }

    public TipoRespuesta getTipoRespuesta() {
        return tipoRespuesta;
    }

    public String getInstruccionesRef() {
        return instruccionesRef;
    }

    public String getPracticaRef() {
        return practicaRef;
    }

    public Integer getMaximoEntradas() {
        return maximoEntradas;
    }
}
