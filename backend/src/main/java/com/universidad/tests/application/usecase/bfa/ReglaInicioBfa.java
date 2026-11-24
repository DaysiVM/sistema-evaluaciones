package com.universidad.tests.application.usecase.bfa;

import com.universidad.tests.application.usecase.ContextoInicio;
import com.universidad.tests.application.usecase.ReglaInicioTest;
import com.universidad.tests.domain.exception.ErroresEvaluacion;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.repository.bfa.AplicacionFluidezBfaRepository;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Regla de E3 para BFA: si la evaluación incluye Fluidez Verbal debe tener letra inicial
 * (RF-FV-06). La letra queda registrada en la ejecución FV (ENT-BFA-11).
 */
@Component
public class ReglaInicioBfa implements ReglaInicioTest {

    static final String CODIGO_TEST = "BFA";
    static final String CODIGO_FLUIDEZ = "FV";
    private static final Pattern UNA_LETRA = Pattern.compile("^[A-ZÑ]$");

    private final AplicacionFluidezBfaRepository fluidezRepository;

    public ReglaInicioBfa(AplicacionFluidezBfaRepository fluidezRepository) {
        this.fluidezRepository = fluidezRepository;
    }

    @Override
    public boolean aplicaA(String codigoTest) {
        return CODIGO_TEST.equals(codigoTest);
    }

    @Override
    public void validar(ContextoInicio contexto) {
        if (!contexto.incluye(CODIGO_FLUIDEZ)) {
            return;
        }
        String letra = normalizar(contexto.letraInicial());
        if (letra == null) {
            throw ErroresEvaluacion.letraNoAsignada();
        }
        if (!UNA_LETRA.matcher(letra).matches()) {
            throw ErroresEvaluacion.solicitudInvalida(
                    "fluidez.letraInicial", "debe ser una sola letra del alfabeto");
        }
    }

    @Override
    public void registrar(ContextoInicio contexto, Evaluacion evaluacionGuardada) {
        if (!contexto.incluye(CODIGO_FLUIDEZ)) {
            return;
        }
        EvaluacionSubprueba fluidez =
                evaluacionGuardada
                        .buscarSubprueba(CODIGO_FLUIDEZ)
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "La evaluación guardada no contiene FV"));
        fluidezRepository.registrarLetra(
                fluidez.getIdEvaluacionSubprueba(), normalizar(contexto.letraInicial()));
    }

    private static String normalizar(String letra) {
        if (letra == null || letra.isBlank()) {
            return null;
        }
        return letra.strip().toUpperCase(Locale.ROOT);
    }
}
