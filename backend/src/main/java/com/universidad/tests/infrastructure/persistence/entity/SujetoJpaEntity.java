package com.universidad.tests.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "sujeto")
public class SujetoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sujeto")
    private Long idSujeto;

    @Column(name = "codigo_institucional", length = 80)
    private String codigoInstitucional;

    @Column(name = "nombres", nullable = false, length = 200)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 200)
    private String apellidos;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "sexo_referencia", length = 40)
    private String sexoReferencia;

    protected SujetoJpaEntity() {
        // Constructor requerido por JPA
    }

    public SujetoJpaEntity(
            String codigoInstitucional,
            String nombres,
            String apellidos,
            LocalDate fechaNacimiento) {
        this.codigoInstitucional = codigoInstitucional;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getIdSujeto() {
        return idSujeto;
    }

    public String getCodigoInstitucional() {
        return codigoInstitucional;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getSexoReferencia() {
        return sexoReferencia;
    }
}
