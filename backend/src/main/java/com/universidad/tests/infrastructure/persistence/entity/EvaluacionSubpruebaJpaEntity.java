package com.universidad.tests.infrastructure.persistence.entity;

import com.universidad.tests.domain.model.EstadoSubprueba;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "evaluacion_subprueba")
public class EvaluacionSubpruebaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion_subprueba")
    private Long idEvaluacionSubprueba;

    @Column(name = "id_evaluacion", nullable = false)
    private Long idEvaluacion;

    @Column(name = "id_configuracion_subprueba", nullable = false)
    private Long idConfiguracionSubprueba;

    @Column(name = "codigo", nullable = false, length = 80)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "orden", nullable = false)
    private int orden;

    @Column(name = "duracion_segundos", nullable = false)
    private int duracionSegundos;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSubprueba estado;

    @Column(name = "instrucciones", columnDefinition = "text")
    private String instrucciones;

    @Column(name = "permitir_retroceso", nullable = false)
    private boolean permitirRetroceso;

    @Column(name = "fecha_inicio")
    private Instant fechaInicio;

    @Column(name = "fecha_fin_limite")
    private Instant fechaFinLimite;

    @Column(name = "fecha_cierre")
    private Instant fechaCierre;

    @Column(name = "letra_inicial", length = 1)
    private String letraInicial;

    @Column(name = "maximo_entradas", nullable = false)
    private int maximoEntradas;

    protected EvaluacionSubpruebaJpaEntity() {
        // Constructor requerido por JPA
    }

    public EvaluacionSubpruebaJpaEntity(
            Long idEvaluacion,
            Long idConfiguracionSubprueba,
            String codigo,
            String nombre,
            int orden,
            int duracionSegundos,
            EstadoSubprueba estado,
            String instrucciones,
            boolean permitirRetroceso,
            Instant fechaInicio,
            Instant fechaFinLimite,
            Instant fechaCierre,
            String letraInicial,
            int maximoEntradas) {
        this.idEvaluacion = idEvaluacion;
        this.idConfiguracionSubprueba = idConfiguracionSubprueba;
        this.codigo = codigo;
        this.nombre = nombre;
        this.orden = orden;
        this.duracionSegundos = duracionSegundos;
        this.estado = estado;
        this.instrucciones = instrucciones;
        this.permitirRetroceso = permitirRetroceso;
        this.fechaInicio = fechaInicio;
        this.fechaFinLimite = fechaFinLimite;
        this.fechaCierre = fechaCierre;
        this.letraInicial = letraInicial;
        this.maximoEntradas = maximoEntradas;
    }

    public Long getIdEvaluacionSubprueba() {
        return idEvaluacionSubprueba;
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public Long getIdConfiguracionSubprueba() {
        return idConfiguracionSubprueba;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getOrden() {
        return orden;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public EstadoSubprueba getEstado() {
        return estado;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public boolean isPermitirRetroceso() {
        return permitirRetroceso;
    }

    public Instant getFechaInicio() {
        return fechaInicio;
    }

    public Instant getFechaFinLimite() {
        return fechaFinLimite;
    }

    public Instant getFechaCierre() {
        return fechaCierre;
    }

    public void iniciar(Instant inicio, Instant limite) {
        estado = EstadoSubprueba.ACTIVA;
        fechaInicio = inicio;
        fechaFinLimite = limite;
    }

    public void setLetraInicial(String letraInicial) {
        this.letraInicial = letraInicial;
    }

    public String getLetraInicial() {
        return letraInicial;
    }

    public int getMaximoEntradas() {
        return maximoEntradas;
    }
}
