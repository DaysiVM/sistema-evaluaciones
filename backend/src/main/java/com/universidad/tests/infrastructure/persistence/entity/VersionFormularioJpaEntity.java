package com.universidad.tests.infrastructure.persistence.entity;

import com.universidad.tests.domain.model.EstadoVersionFormulario;
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
import java.time.Instant;

@Entity
@Table(name = "version_formulario")
public class VersionFormularioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_version_formulario")
    private Long idVersionFormulario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_test", nullable = false)
    private TestJpaEntity test;

    @Column(name = "codigo_version", nullable = false, length = 80)
    private String codigoVersion;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoVersionFormulario estado;

    @Column(name = "fuente_ref", nullable = false, columnDefinition = "text")
    private String fuenteRef;

    @Column(name = "publicada_en")
    private Instant publicadaEn;

    @Column(name = "evidencia_validacion", columnDefinition = "text")
    private String evidenciaValidacion;

    protected VersionFormularioJpaEntity() {
        // Constructor requerido por JPA
    }

    public VersionFormularioJpaEntity(
            TestJpaEntity test, String codigoVersion, String descripcion, String fuenteRef) {

        if (test == null) {
            throw new IllegalArgumentException("La version debe pertenecer a un test");
        }

        if (codigoVersion == null || codigoVersion.isBlank()) {
            throw new IllegalArgumentException("El codigo de version es obligatorio");
        }

        if (fuenteRef == null || fuenteRef.isBlank()) {
            throw new IllegalArgumentException("La referencia de la fuente es obligatoria");
        }

        this.test = test;
        this.codigoVersion = codigoVersion;
        this.descripcion = descripcion;
        this.fuenteRef = fuenteRef;
        this.estado = EstadoVersionFormulario.BORRADOR;
    }

    public Long getIdVersionFormulario() {
        return idVersionFormulario;
    }

    public TestJpaEntity getTest() {
        return test;
    }

    public Long getIdTest() {
        return test.getIdTest();
    }

    public String getCodigoVersion() {
        return codigoVersion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoVersionFormulario getEstado() {
        return estado;
    }

    public String getFuenteRef() {
        return fuenteRef;
    }

    public Instant getPublicadaEn() {
        return publicadaEn;
    }

    public String getEvidenciaValidacion() {
        return evidenciaValidacion;
    }

    public void aplicarEstado(
            EstadoVersionFormulario nuevoEstado,
            java.time.Instant fechaPublicacion,
            String evidencia) {

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        if (this.estado == nuevoEstado) {
            return;
        }

        if (this.estado == EstadoVersionFormulario.BORRADOR
                && nuevoEstado == EstadoVersionFormulario.PUBLICADA) {

            if (fechaPublicacion == null || evidencia == null || evidencia.isBlank()) {
                throw new IllegalArgumentException("La publicacion requiere fecha y evidencia");
            }

            this.estado = EstadoVersionFormulario.PUBLICADA;
            this.publicadaEn = fechaPublicacion;
            this.evidenciaValidacion = evidencia;
            return;
        }

        if (this.estado == EstadoVersionFormulario.PUBLICADA
                && nuevoEstado == EstadoVersionFormulario.RETIRADA) {

            this.estado = EstadoVersionFormulario.RETIRADA;
            return;
        }

        throw new IllegalStateException(
                "Transicion de estado no permitida: " + this.estado + " -> " + nuevoEstado);
    }
}
