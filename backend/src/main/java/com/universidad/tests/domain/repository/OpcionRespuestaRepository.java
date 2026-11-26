package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.OpcionRespuesta;
import java.util.List;
import java.util.Optional;

public interface OpcionRespuestaRepository {

    Optional<OpcionRespuesta> buscarPorId(Long idOpcion);

    List<OpcionRespuesta> listarPorPregunta(Long idPregunta);

    // Cantidad de opciones de cada pregunta que tiene opciones en la configuracion
    List<Long> contarPorPreguntaDeConfiguracion(Long idConfiguracionSubprueba);

    Optional<OpcionRespuesta> buscarPorCodigo(Long idPregunta, String codigo);

    boolean existePorCodigo(Long idPregunta, String codigo);

    OpcionRespuesta guardar(OpcionRespuesta opcion);

    interface Edicion extends OpcionRespuestaRepository {
        void bloquearVersion(Long idVersion);

        void eliminarPorId(Long idOpcion);
    }
}
