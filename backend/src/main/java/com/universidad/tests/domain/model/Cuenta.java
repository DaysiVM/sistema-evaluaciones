package com.universidad.tests.domain.model;

import java.util.EnumSet;
import java.util.Set;

// ENT-COM-14: credencial de acceso de una persona; no reemplaza al Sujeto.
// La contrasena solo se conserva como resumen cifrado y nunca se devuelve.
public class Cuenta {

    private final Long idCuenta;
    private final String correo;
    private final String resumenContrasena;
    private final String nombres;
    private final String apellidos;
    private final EstadoCuenta estado;
    private final Set<Rol> roles;

    public Cuenta(
            Long idCuenta,
            String correo,
            String resumenContrasena,
            String nombres,
            String apellidos,
            EstadoCuenta estado,
            Set<Rol> roles) {

        this.idCuenta = idCuenta;
        this.correo = obligatorio(correo, "El correo es obligatorio");
        this.resumenContrasena =
                obligatorio(resumenContrasena, "El resumen de la contrasena es obligatorio");
        this.nombres = obligatorio(nombres, "Los nombres son obligatorios");
        this.apellidos = obligatorio(apellidos, "Los apellidos son obligatorios");
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la cuenta es obligatorio");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("La cuenta necesita al menos un rol");
        }
        this.estado = estado;
        this.roles = Set.copyOf(EnumSet.copyOf(roles));
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

    public EstadoCuenta getEstado() {
        return estado;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public boolean estaActiva() {
        return estado == EstadoCuenta.ACTIVA;
    }

    @Override
    public String toString() {
        return "Cuenta[idCuenta=" + idCuenta + ", estado=" + estado + "]";
    }

    private static String obligatorio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }
}
