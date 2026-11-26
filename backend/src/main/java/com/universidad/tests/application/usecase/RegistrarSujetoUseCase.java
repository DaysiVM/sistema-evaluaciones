package com.universidad.tests.application.usecase;

import com.universidad.tests.application.usecase.vista.SujetoVista;
import com.universidad.tests.domain.exception.ErrorNegocioException;
import com.universidad.tests.domain.exception.TipoErrorNegocio;
import com.universidad.tests.domain.model.Sujeto;
import com.universidad.tests.domain.port.AutorizacionSujetoPort;
import com.universidad.tests.domain.repository.SujetoRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** E18 — Registrar a la persona evaluada sin crear cuenta ni abrir evaluación (CU-GEN-20). */
@Service
public class RegistrarSujetoUseCase {

    private final AutorizacionSujetoPort autorizacion;
    private final SujetoRepository sujetoRepository;

    public RegistrarSujetoUseCase(
            AutorizacionSujetoPort autorizacion, SujetoRepository sujetoRepository) {
        this.autorizacion = autorizacion;
        this.sujetoRepository = sujetoRepository;
    }

    @Transactional
    public SujetoVista registrar(
            String identificadorInstitucional,
            String nombres,
            String apellidos,
            LocalDate fechaNacimiento) {
        autorizacion.exigirPermisoCrearSujeto();

        String codigoLimpio =
                identificadorInstitucional == null ? null : identificadorInstitucional.trim();
        String nombresLimpios = nombres == null ? null : nombres.trim();
        String apellidosLimpios = apellidos == null ? null : apellidos.trim();

        if (codigoLimpio != null
                && sujetoRepository.buscarPorCodigoInstitucional(codigoLimpio).isPresent()) {
            throw new ErrorNegocioException(
                    TipoErrorNegocio.CONFLICTO,
                    "SUJETO_DUPLICADO",
                    "Ya existe un sujeto registrado con el identificador institucional: "
                            + codigoLimpio);
        }

        Sujeto nuevo =
                new Sujeto(null, codigoLimpio, nombresLimpios, apellidosLimpios, fechaNacimiento);
        Sujeto guardado = sujetoRepository.guardar(nuevo);
        return new SujetoVista(
                guardado.idSujeto(),
                guardado.codigoInstitucional(),
                guardado.nombres(),
                guardado.apellidos());
    }
}
