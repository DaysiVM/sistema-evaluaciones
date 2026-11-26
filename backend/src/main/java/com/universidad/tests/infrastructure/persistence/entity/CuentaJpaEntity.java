package com.universidad.tests.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cuenta")
public class CuentaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Long idCuenta;

    @Column(name = "correo", nullable = false, length = 254)
    private String correo;

    @Column(name = "resumen_contrasena", nullable = false, length = 200)
    private String resumenContrasena;

    @Column(name = "nombres", nullable = false, length = 120)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 120)
    private String apellidos;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cuenta_rol", joinColumns = @JoinColumn(name = "id_cuenta"))
    @Column(name = "rol", nullable = false, length = 20)
    private Set<String> roles = new HashSet<>();

    protected CuentaJpaEntity() {
        // Constructor requerido por JPA
    }

    public CuentaJpaEntity(
            String correo,
            String resumenContrasena,
            String nombres,
            String apellidos,
            String estado,
            Set<String> roles) {

        this.correo = correo;
        actualizarDatos(resumenContrasena, nombres, apellidos, estado, roles);
    }

    public void actualizarDatos(
            String resumenContrasena,
            String nombres,
            String apellidos,
            String estado,
            Set<String> roles) {

        this.resumenContrasena = resumenContrasena;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.estado = estado;
        this.roles.clear();
        this.roles.addAll(roles);
    }

    public Long getIdCuenta() {
        return idCuenta;
    }

    public String getCorreo() {
        return correo;
    }

    public String getResumenContrasena() {
        return resumenContrasena;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEstado() {
        return estado;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
