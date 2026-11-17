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
@Table(name = "subprueba")
public class SubpruebaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_subprueba")
    private Long idSubprueba;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_test", nullable = false)
    private TestJpaEntity test;

    @Column(name = "codigo", nullable = false, length = 80)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    protected SubpruebaJpaEntity() {
        // Constructor requerido por JPA
    }

    public SubpruebaJpaEntity(
            TestJpaEntity test, String codigo, String nombre, String descripcion) {

        if (test == null) {
            throw new IllegalArgumentException("La subprueba debe pertenecer a un test");
        }

        this.test = test;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public void actualizarDatos(String codigo, String nombre, String descripcion) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo de la subprueba es obligatorio");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la subprueba es obligatorio");
        }

        if (descripcion != null && descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion no puede estar vacia");
        }

        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getIdSubprueba() {
        return idSubprueba;
    }

    public TestJpaEntity getTest() {
        return test;
    }

    public Long getIdTest() {
        return test.getIdTest();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
