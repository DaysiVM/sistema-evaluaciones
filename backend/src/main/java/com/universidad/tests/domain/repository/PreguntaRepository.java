package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Pregunta;
import java.util.List;
import java.util.Optional;

public interface PreguntaRepository {

    Optional<Pregunta> buscarPorId(Long idPregunta);

    List<Pregunta> listarPorConfiguracion(Long idConfiguracionSubprueba);

    Optional<Pregunta> buscarPorNumeroVisible(Long idConfiguracionSubprueba, String numeroVisible);

    boolean existePorNumeroVisible(Long idConfiguracionSubprueba, String numeroVisible);

    Pregunta guardar(Pregunta pregunta);
}
