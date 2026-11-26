package com.universidad.tests.application.usecase;

import com.universidad.tests.application.service.ContextoSesion;
import com.universidad.tests.application.usecase.vista.SujetoVista;
import com.universidad.tests.domain.exception.ErroresEvaluacion;
import com.universidad.tests.domain.model.Sujeto;
import com.universidad.tests.domain.port.AutorizacionEvaluacionPort;
import com.universidad.tests.domain.repository.SujetoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** E19 — Consultar un Sujeto. Una consulta nunca crea el registro (CU-GEN-20). */
@Service
public class ConsultarSujetoUseCase {

    private final AutorizacionEvaluacionPort autorizacion;
    private final SujetoRepository sujetoRepository;

    public ConsultarSujetoUseCase(
            AutorizacionEvaluacionPort autorizacion, SujetoRepository sujetoRepository) {
        this.autorizacion = autorizacion;
        this.sujetoRepository = sujetoRepository;
    }

    @Transactional(readOnly = true)
    public SujetoVista consultar(Long idSujeto) {
        autorizacion.exigirPermisoLeerSujeto(idSujeto);
        Sujeto sujeto =
                sujetoRepository
                        .buscarPorId(idSujeto)
                        .orElseThrow(() -> ErroresEvaluacion.sujetoNoEncontrado(idSujeto));
        if (ContextoSesion.actual().map(i -> "DECANO".equals(i.rolActivo())).orElse(false)) {
            return new SujetoVista(sujeto.idSujeto(), sujeto.codigoInstitucional(), null, null);
        }
        return new SujetoVista(
                sujeto.idSujeto(),
                sujeto.codigoInstitucional(),
                sujeto.nombres(),
                sujeto.apellidos());
    }
}
