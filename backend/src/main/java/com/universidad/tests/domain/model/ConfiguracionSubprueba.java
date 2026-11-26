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
    private final Integer opcionesEsperadas;

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
        this(
                idConfiguracionSubprueba,
                idVersionFormulario,
                idSubprueba,
                ordenAplicacion,
                tiempoLimiteSegundos,
                tipoRespuesta,
                instruccionesRef,
                practicaRef,
                maximoEntradas,
                null);
    }

    public ConfiguracionSubprueba(
            Long idConfiguracionSubprueba,
            Long idVersionFormulario,
            Long idSubprueba,
            int ordenAplicacion,
            Integer tiempoLimiteSegundos,
            TipoRespuesta tipoRespuesta,
            String instruccionesRef,
            String practicaRef,
            Integer maximoEntradas,
            Integer opcionesEsperadas) {

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

        if (opcionesEsperadas != null && opcionesEsperadas <= 0) {
            throw new IllegalArgumentException("El numero de opciones debe ser positivo");
        }

        if (tipoRespuesta == TipoRespuesta.TEXTO_LIBRE && opcionesEsperadas != null) {
            throw new IllegalArgumentException("TEXTO_LIBRE no utiliza opciones cerradas");
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
        this.opcionesEsperadas = opcionesEsperadas;
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

    public Integer getOpcionesEsperadas() {
        return opcionesEsperadas;
    }
}
