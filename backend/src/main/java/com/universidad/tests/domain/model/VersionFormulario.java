package com.universidad.tests.domain.model;

import java.time.Instant;

public class VersionFormulario {

    private final Long idVersionFormulario;
    private final Long idTest;
    private final String codigoVersion;
    private final String descripcion;
    private final String fuenteRef;

    private EstadoVersionFormulario estado;
    private Instant publicadaEn;
    private String evidenciaValidacion;

    public VersionFormulario(
            Long idVersionFormulario,
            Long idTest,
            String codigoVersion,
            String descripcion,
            String fuenteRef) {

        if (idTest == null || idTest <= 0) {
            throw new IllegalArgumentException("La version debe pertenecer a un test valido");
        }

        validarTexto(codigoVersion, "codigoVersion");
        validarTexto(fuenteRef, "fuenteRef");

        if (descripcion != null && descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion no puede estar vacia");
        }

        this.idVersionFormulario = idVersionFormulario;
        this.idTest = idTest;
        this.codigoVersion = codigoVersion;
        this.descripcion = descripcion;
        this.fuenteRef = fuenteRef;

        this.estado = EstadoVersionFormulario.BORRADOR;
        this.publicadaEn = null;
        this.evidenciaValidacion = null;
    }

    private static void validarTexto(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio");
        }
    }

    // Publicar una version en estado BORRADOR
    public void publicar(String evidenciaValidacion) {

        if (this.estado != EstadoVersionFormulario.BORRADOR) {
            throw new IllegalStateException("Solo se puede publicar una version en BORRADOR");
        }

        validarTexto(evidenciaValidacion, "evidenciaValidacion");

        this.publicadaEn = Instant.now();
        this.evidenciaValidacion = evidenciaValidacion;
        this.estado = EstadoVersionFormulario.PUBLICADA;
    }

    // Retirar una version previamente publicada
    public void retirar() {

        if (this.estado != EstadoVersionFormulario.PUBLICADA) {
            throw new IllegalStateException("Solo se puede retirar una version PUBLICADA");
        }

        this.estado = EstadoVersionFormulario.RETIRADA;
    }

    // Consultar si la version esta publicada
    public boolean estaPublicada() {
        return this.estado == EstadoVersionFormulario.PUBLICADA;
    }

    // Consultar si la version permite modificaciones
    public boolean esEditable() {
        return this.estado == EstadoVersionFormulario.BORRADOR;
    }

    public Long getIdVersionFormulario() {
        return idVersionFormulario;
    }

    public Long getIdTest() {
        return idTest;
    }

    public String getCodigoVersion() {
        return codigoVersion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFuenteRef() {
        return fuenteRef;
    }

    public EstadoVersionFormulario getEstado() {
        return estado;
    }

    public Instant getPublicadaEn() {
        return publicadaEn;
    }

    public String getEvidenciaValidacion() {
        return evidenciaValidacion;
    }

    // Reconstruir una version existente desde PostgreSQL
    public static VersionFormulario restaurar(
            Long idVersionFormulario,
            Long idTest,
            String codigoVersion,
            String descripcion,
            String fuenteRef,
            EstadoVersionFormulario estado,
            Instant publicadaEn,
            String evidenciaValidacion) {

        if (estado == null) {
            throw new IllegalArgumentException("El estado de la version es obligatorio");
        }

        if (estado != EstadoVersionFormulario.BORRADOR) {

            if (publicadaEn == null) {
                throw new IllegalArgumentException(
                        "Una version publicada o retirada " + "debe tener fecha de publicacion");
            }

            validarTexto(evidenciaValidacion, "evidenciaValidacion");

        } else if (publicadaEn != null || evidenciaValidacion != null) {

            throw new IllegalArgumentException(
                    "Una version en BORRADOR no debe tener " + "datos de publicacion");
        }

        VersionFormulario version =
                new VersionFormulario(
                        idVersionFormulario, idTest, codigoVersion, descripcion, fuenteRef);

        version.estado = estado;
        version.publicadaEn = publicadaEn;
        version.evidenciaValidacion = evidenciaValidacion;

        return version;
    }
}
