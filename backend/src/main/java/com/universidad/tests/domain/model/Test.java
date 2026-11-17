package com.universidad.tests.domain.model;

public class Test {

    private final Long idTest;
    private String codigo;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public Test(Long idTest, String codigo, String nombre, String descripcion, boolean activo) {
        this.idTest = idTest;
        setCodigo(codigo);
        setNombre(nombre);
        setDescripcion(descripcion);
        this.activo = activo;
    }

    public Long getIdTest() {
        return idTest;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo del test es obligatorio");
        }
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del test es obligatorio");
        }
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion != null && descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion no puede estar vacia");
        }
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
