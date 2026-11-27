package com.universidad.tests.application.service;

import static com.universidad.tests.domain.model.MatrizPermisos.OPERACION_AUTORIZAR;
import static com.universidad.tests.domain.model.MatrizPermisos.RECUPERAR_INTENTO;

import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.domain.repository.ListadoEvaluacionesRepository;
import com.universidad.tests.dto.request.EstadoListadoEvaluacion;
import com.universidad.tests.dto.response.EvaluacionResponse;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListadoEvaluacionesService {
    private final AutorizacionService autorizacion;
    private final ListadoEvaluacionesRepository listado;
    private final EvaluacionRepository evaluaciones;
    private final Clock reloj;

    public ListadoEvaluacionesService(
            AutorizacionService autorizacion,
            ListadoEvaluacionesRepository listado,
            EvaluacionRepository evaluaciones,
            Clock reloj) {
        this.autorizacion = autorizacion;
        this.listado = listado;
        this.evaluaciones = evaluaciones;
        this.reloj = reloj;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<EvaluacionResponse> listar(
            Long sujetoId, Long testId, Long periodoId, EstadoListadoEvaluacion estado) {
        var sesion = autorizacion.exigirSesion();
        if (!sesion.tienePermiso(RECUPERAR_INTENTO) && !sesion.tienePermiso(OPERACION_AUTORIZAR)) {
            throw new SinPermisoException();
        }

        Long cuentaRestringida = null;
        if (Rol.SUJETO.name().equals(sesion.rolActivo())) {
            cuentaRestringida = sesion.idCuenta();
            if (cuentaRestringida == null) {
                throw new SinPermisoException();
            }
        }

        Instant ahora = reloj.instant();
        var ids =
                listado.buscarIds(
                        cuentaRestringida,
                        sujetoId,
                        testId,
                        periodoId,
                        estado == null ? null : EstadoEvaluacion.valueOf(estado.name()));
        return evaluaciones.buscarPorIds(ids).stream()
                .sorted(Comparator.comparing(Evaluacion::idEvaluacion))
                .map(evaluacion -> EvaluacionResponseMapper.aRespuesta(evaluacion, ahora))
                .toList();
    }
}
