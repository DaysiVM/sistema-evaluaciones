package com.universidad.tests.application.usecase;

import com.universidad.tests.application.usecase.vista.PeriodoVista;
import com.universidad.tests.domain.port.AutorizacionEvaluacionPort;
import com.universidad.tests.domain.repository.PeriodoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** E21 — Listar períodos; con disponible=true, solo los que admiten nuevas evaluaciones. */
@Service
public class ListarPeriodosUseCase {

    private final AutorizacionEvaluacionPort autorizacion;
    private final PeriodoRepository periodoRepository;

    public ListarPeriodosUseCase(
            AutorizacionEvaluacionPort autorizacion, PeriodoRepository periodoRepository) {
        this.autorizacion = autorizacion;
        this.periodoRepository = periodoRepository;
    }

    @Transactional(readOnly = true)
    public List<PeriodoVista> listar(boolean soloDisponibles) {
        autorizacion.exigirPermisoLeerPeriodos();
        return periodoRepository.listar(soloDisponibles).stream()
                .map(
                        p ->
                                new PeriodoVista(
                                        p.idPeriodo(),
                                        p.codigo(),
                                        p.nombre(),
                                        p.fechaInicio(),
                                        p.fechaFin(),
                                        p.admiteNuevasEvaluaciones()))
                .toList();
    }
}
