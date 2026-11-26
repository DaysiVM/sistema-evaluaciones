package com.universidad.tests.domain.port;

/**
 * Permisos de GEN-02 (Documento general §5.2.1). La sesión y los roles llegan con GEN-06; hasta
 * entonces la infraestructura aporta una implementación provisional.
 */
public interface AutorizacionEvaluacionPort {

    /** Crear una evaluación para un sujeto (E3). */
    void exigirPermisoIniciarEvaluacion(Long idSujeto);

    /** Consultar o retomar una evaluación (E4). */
    void exigirPermisoConsultarEvaluacion(Long idEvaluacion, Long idSujeto);

    /** SUJETO_LEER (E19). */
    void exigirPermisoLeerSujeto(Long idSujeto);

    /** PERIODO_LEER (E21). */
    void exigirPermisoLeerPeriodos();
}
