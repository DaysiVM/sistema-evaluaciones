package com.universidad.tests.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "sesion")
public class SesionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Long idSesion;

    @Column(name = "id_cuenta", nullable = false)
    private Long idCuenta;

    @Column(name = "resumen_token", nullable = false, length = 64, columnDefinition = "bpchar(64)")
    private String resumenToken;

    @Column(name = "rol_activo", nullable = false, length = 20)
    private String rolActivo;

    @Column(name = "abierta_en", nullable = false)
    private Instant abiertaEn;

    @Column(name = "expira_en")
    private Instant expiraEn;

    @Column(name = "cerrada_en")
    private Instant cerradaEn;

    protected SesionJpaEntity() {
        // Constructor requerido por JPA
    }

    public SesionJpaEntity(
            Long idCuenta,
            String resumenToken,
            String rolActivo,
            Instant abiertaEn,
            Instant expiraEn,
            Instant cerradaEn) {

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

    public String getRolActivo() {
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
}
