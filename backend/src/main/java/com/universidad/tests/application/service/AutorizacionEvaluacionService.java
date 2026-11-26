package com.universidad.tests.application.service;

import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.domain.port.*;
import org.springframework.stereotype.Service;

/** Permiso de operación más asignación explícita: ningún rol tiene alcance global. */
@Service
public class AutorizacionEvaluacionService implements AutorizacionEvaluacionPort {
    private final AutorizacionService autorizacion;
    private final AlcanceSujetoPort alcance;

    public AutorizacionEvaluacionService(
            AutorizacionService autorizacion, AlcanceSujetoPort alcance) {
        this.autorizacion = autorizacion;
        this.alcance = alcance;
    }

    public void exigirPermisoIniciarEvaluacion(Long sujeto) {
        var sesion = autorizacion.exigirPermiso("OPERACION_AUTORIZAR");
        exigirAsignacion(sesion, sujeto);
    }

    public void exigirPermisoConsultarEvaluacion(Long evaluacion, Long sujeto) {
        var sesion = autorizacion.exigirSesion();
        if (!sesion.tienePermiso("RECUPERAR_INTENTO")
                && !sesion.tienePermiso("OPERACION_AUTORIZAR")) throw new SinPermisoException();
        exigirAsignacion(sesion, sujeto);
    }

    public void exigirPermisoLeerSujeto(Long sujeto) {
        exigirAsignacion(autorizacion.exigirPermiso("SUJETO_LEER"), sujeto);
    }

    public void exigirPermisoLeerPeriodos() {
        autorizacion.exigirPermiso("PERIODO_LEER");
    }

    private void exigirAsignacion(IdentidadSesion sesion, Long sujeto) {
        if (!alcance.estaAsignado(sesion.idCuenta(), sujeto)) throw new SinPermisoException();
    }
}
