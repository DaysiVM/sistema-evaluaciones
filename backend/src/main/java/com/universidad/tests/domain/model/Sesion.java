package com.universidad.tests.domain.model;

import java.time.Instant;

// Sesion abierta con E14 y cerrada con E15. Solo se conserva el resumen del token.
public class Sesion {

    private final Long idSesion;
    private final Long idCuenta;
    private final String resumenToken;
    private final Rol rolActivo;
    private final Instant abiertaEn;
    private final Instant expiraEn;
    private final Instant cerradaEn;

    public Sesion(
            Long idSesion,
            Long idCuenta,
            String resumenToken,
            Rol rolActivo,
            Instant abiertaEn,
            Instant expiraEn,
            Instant cerradaEn) {

        if (idCuenta == null) {
            throw new IllegalArgumentException("La cuenta de la sesion es obligatoria");
        }
        if (resumenToken == null || resumenToken.isBlank()) {
            throw new IllegalArgumentException("El resumen del token es obligatorio");
        }
        if (rolActivo == null) {
            throw new IllegalArgumentException("El rol activo es obligatorio");
        }
        if (abiertaEn == null) {
            throw new IllegalArgumentException("La fecha de apertura es obligatoria");
        }
        this.idSesion = idSesion;
        this.idCuenta = idCuenta;
        this.resumenToken = resumenToken;
        this.rolActivo = rolActivo;
        this.abiertaEn = abiertaEn;
        this.expiraEn = expiraEn;
        this.cerradaEn = cerradaEn;
    }

    public Long getIdSesion() {
        return idSesion;
    }

    public Long getIdCuenta() {
        return idCuenta;
    }

    public String getResumenToken() {
        return resumenToken;
    }

    public Rol getRolActivo() {
        return rolActivo;
    }

    public Instant getAbiertaEn() {
        return abiertaEn;
    }

    public Instant getExpiraEn() {
        return expiraEn;
    }

    public Instant getCerradaEn() {
        return cerradaEn;
    }

    // Vigente si no se cerro y, cuando tiene vencimiento, aun no vence
    public boolean estaVigente(Instant ahora) {
        return cerradaEn == null && (expiraEn == null || ahora.isBefore(expiraEn));
    }

    @Override
    public String toString() {
        return "Sesion[idSesion=" + idSesion + ", rolActivo=" + rolActivo + "]";
    }
}
