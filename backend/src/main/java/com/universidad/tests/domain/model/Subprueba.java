package com.universidad.tests.domain.model;

public class Subprueba {

    private final Long idSubprueba;
    private final Long idTest;
    private String codigo;
    private String nombre;
    private String descripcion;

    public Subprueba(
            Long idSubprueba, Long idTest, String codigo, String nombre, String descripcion) {

        if (idTest == null) {
            throw new IllegalArgumentException("La subprueba debe pertenecer a un test");
        }

        this.idSubprueba = idSubprueba;
        this.idTest = idTest;

        setCodigo(codigo);
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    public Long getIdSubprueba() {
        return idSubprueba;
    }

    public Long getIdTest() {
        return idTest;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo de la subprueba es obligatorio");
        }
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la subprueba es obligatorio");
        }
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
