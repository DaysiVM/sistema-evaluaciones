package com.universidad.tests.infrastructure.persistence.entity;

import com.universidad.tests.domain.model.TipoRespuesta;
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

@Entity
@Table(name = "configuracion_subprueba")
public class ConfiguracionSubpruebaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion_subprueba")
    private Long idConfiguracionSubprueba;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_version_formulario", nullable = false)
    private VersionFormularioJpaEntity versionFormulario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_subprueba", nullable = false)
    private SubpruebaJpaEntity subprueba;

    @Column(name = "orden_aplicacion", nullable = false)
    private int ordenAplicacion;

    @Column(name = "tiempo_limite_segundos")
    private Integer tiempoLimiteSegundos;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_respuesta", nullable = false, length = 20)
    private TipoRespuesta tipoRespuesta;

    @Column(name = "instrucciones_ref", nullable = false, columnDefinition = "text")
    private String instruccionesRef;

    @Column(name = "practica_ref", columnDefinition = "text")
    private String practicaRef;

    @Column(name = "maximo_entradas")
    private Integer maximoEntradas;

    protected ConfiguracionSubpruebaJpaEntity() {
        // Constructor requerido por JPA
    }

    public ConfiguracionSubpruebaJpaEntity(
            VersionFormularioJpaEntity versionFormulario,
            SubpruebaJpaEntity subprueba,
            int ordenAplicacion,
            Integer tiempoLimiteSegundos,
            TipoRespuesta tipoRespuesta,
            String instruccionesRef,
            String practicaRef,
            Integer maximoEntradas) {

        if (versionFormulario == null) {
            throw new IllegalArgumentException("La version del formulario es obligatoria");
        }

        if (subprueba == null) {
            throw new IllegalArgumentException("La subprueba es obligatoria");
        }

        if (ordenAplicacion <= 0) {
            throw new IllegalArgumentException("El orden debe ser positivo");
        }

        if (tiempoLimiteSegundos != null && tiempoLimiteSegundos <= 0) {
            throw new IllegalArgumentException("El tiempo limite debe ser positivo");
        }

        if (tipoRespuesta == null) {
            throw new IllegalArgumentException("El tipo de respuesta es obligatorio");
        }

        if (instruccionesRef == null || instruccionesRef.isBlank()) {
            throw new IllegalArgumentException("Las instrucciones son obligatorias");
        }

        if (practicaRef != null && practicaRef.isBlank()) {
            throw new IllegalArgumentException("La referencia de practica no puede estar vacia");
        }

        if (maximoEntradas != null && maximoEntradas <= 0) {
            throw new IllegalArgumentException("El maximo de entradas debe ser positivo");
        }

        if (tipoRespuesta == TipoRespuesta.SELECCION && maximoEntradas != null) {
            throw new IllegalArgumentException(
                    "Las respuestas de seleccion no utilizan maximoEntradas");
        }

        this.versionFormulario = versionFormulario;
        this.subprueba = subprueba;
        this.ordenAplicacion = ordenAplicacion;
        this.tiempoLimiteSegundos = tiempoLimiteSegundos;
        this.tipoRespuesta = tipoRespuesta;
        this.instruccionesRef = instruccionesRef;
        this.practicaRef = practicaRef;
        this.maximoEntradas = maximoEntradas;
    }

    public Long getIdConfiguracionSubprueba() {
        return idConfiguracionSubprueba;
    }

    public VersionFormularioJpaEntity getVersionFormulario() {
        return versionFormulario;
    }

    public Long getIdVersionFormulario() {
        return versionFormulario.getIdVersionFormulario();
    }

    public SubpruebaJpaEntity getSubprueba() {
        return subprueba;
    }

    public Long getIdSubprueba() {
        return subprueba.getIdSubprueba();
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

    public boolean tieneTiempoLimite() {
        return tiempoLimiteSegundos != null;
    }

    public void actualizarDatos(
            int ordenAplicacion,
            Integer tiempoLimiteSegundos,
            TipoRespuesta tipoRespuesta,
            String instruccionesRef,
            String practicaRef,
            Integer maximoEntradas) {

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

        if (maximoEntradas != null && maximoEntradas <= 0) {
            throw new IllegalArgumentException("El maximo de entradas debe ser positivo");
        }

        if (tipoRespuesta == TipoRespuesta.SELECCION && maximoEntradas != null) {
            throw new IllegalArgumentException("SELECCION no admite maximoEntradas");
        }

        this.ordenAplicacion = ordenAplicacion;
        this.tiempoLimiteSegundos = tiempoLimiteSegundos;
        this.tipoRespuesta = tipoRespuesta;
        this.instruccionesRef = instruccionesRef;
        this.practicaRef = practicaRef;
        this.maximoEntradas = maximoEntradas;
    }
}
