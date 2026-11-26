package com.universidad.tests.application.usecase;

import com.universidad.tests.application.service.AutorizacionService;
import com.universidad.tests.application.usecase.vista.SujetoVista;
import com.universidad.tests.domain.port.AlcanceSujetoPort;
import com.universidad.tests.domain.port.AutorizacionSujetoPort;
import com.universidad.tests.domain.repository.SujetoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** E23 — Buscar un Sujeto por identificador institucional; devuelve 0 o 1 elemento (CU-GEN-20). */
@Service
public class BuscarSujetoUseCase {

    private final AutorizacionSujetoPort autorizacion;
    private final SujetoRepository sujetoRepository;
    private final AlcanceSujetoPort alcance;
    private final AutorizacionService sesiones;

    public BuscarSujetoUseCase(
            AutorizacionSujetoPort autorizacion,
            SujetoRepository sujetoRepository,
            AlcanceSujetoPort alcance,
            AutorizacionService sesiones) {
        this.autorizacion = autorizacion;
        this.sujetoRepository = sujetoRepository;
        this.alcance = alcance;
        this.sesiones = sesiones;
    }

    @Transactional(readOnly = true)
    public List<SujetoVista> buscarPorIdentificador(String identificadorInstitucional) {
        autorizacion.exigirPermisoBuscarSujetos();
        var identidad = sesiones.exigirSesion();
        if (identificadorInstitucional == null || identificadorInstitucional.isBlank()) {
            return List.of();
        }
        return sujetoRepository
                .buscarPorCodigoInstitucional(identificadorInstitucional.trim())
                .filter(s -> alcance.estaAsignado(identidad.idCuenta(), s.idSujeto()))
                .map(
                        s ->
                                List.of(
                                        new SujetoVista(
                                                s.idSujeto(),
                                                s.codigoInstitucional(),
                                                "DECANO".equals(identidad.rolActivo())
                                                        ? null
                                                        : s.nombres(),
                                                "DECANO".equals(identidad.rolActivo())
                                                        ? null
                                                        : s.apellidos())))
                .orElseGet(List::of);
    }
}
