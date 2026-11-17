package com.universidad.tests.application.service;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.OpcionRespuesta;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.OpcionRespuestaRepository;
import com.universidad.tests.domain.repository.PreguntaRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EdicionContenidoVersionadoService {

    private final VersionFormularioRepository versionRepository;
    private final ConfiguracionSubpruebaRepository configuracionRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionRespuestaRepository opcionRepository;
    private final AutorizacionContenidoPort autorizacion;

    public EdicionContenidoVersionadoService(
            VersionFormularioRepository versionRepository,
            ConfiguracionSubpruebaRepository configuracionRepository,
            PreguntaRepository preguntaRepository,
            OpcionRespuestaRepository opcionRepository,
            AutorizacionContenidoPort autorizacion) {

        this.versionRepository = versionRepository;
        this.configuracionRepository = configuracionRepository;
        this.preguntaRepository = preguntaRepository;
        this.opcionRepository = opcionRepository;
        this.autorizacion = autorizacion;
    }

    @Transactional
    public ConfiguracionSubprueba guardarConfiguracion(ConfiguracionSubprueba configuracion) {

        if (configuracion == null) {
            throw new IllegalArgumentException("La configuracion es obligatoria");
        }

        Long idVersion = configuracion.getIdVersionFormulario();

        autorizacion.exigirPermisoEditarContenido(idVersion);

        if (configuracion.getIdConfiguracionSubprueba() != null) {

            ConfiguracionSubprueba existente =
                    configuracionRepository
                            .buscarPorId(configuracion.getIdConfiguracionSubprueba())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la configuracion indicada"));

            validarMismaRelacion(existente.getIdVersionFormulario(), idVersion);

            validarMismaRelacion(existente.getIdSubprueba(), configuracion.getIdSubprueba());
        }

        validarBorrador(buscarVersion(idVersion));

        return configuracionRepository.guardar(configuracion);
    }

    @Transactional
    public Pregunta guardarPregunta(Pregunta pregunta) {

        if (pregunta == null) {
            throw new IllegalArgumentException("La pregunta es obligatoria");
        }

        ConfiguracionSubprueba configuracion =
                buscarConfiguracion(pregunta.getIdConfiguracionSubprueba());

        Long idVersion = configuracion.getIdVersionFormulario();

        autorizacion.exigirPermisoEditarContenido(idVersion);

        if (pregunta.getIdPregunta() != null) {

            Pregunta existente =
                    preguntaRepository
                            .buscarPorId(pregunta.getIdPregunta())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la pregunta indicada"));

            validarMismaRelacion(
                    existente.getIdConfiguracionSubprueba(),
                    pregunta.getIdConfiguracionSubprueba());
        }

        validarBorrador(buscarVersion(idVersion));

        return preguntaRepository.guardar(pregunta);
    }

    @Transactional
    public OpcionRespuesta guardarOpcion(OpcionRespuesta opcion) {

        if (opcion == null) {
            throw new IllegalArgumentException("La opcion de respuesta es obligatoria");
        }

        Pregunta pregunta = buscarPregunta(opcion.getIdPregunta());

        ConfiguracionSubprueba configuracion =
                buscarConfiguracion(pregunta.getIdConfiguracionSubprueba());

        Long idVersion = configuracion.getIdVersionFormulario();

        autorizacion.exigirPermisoEditarContenido(idVersion);

        if (opcion.getIdOpcion() != null) {

            OpcionRespuesta existente =
                    opcionRepository
                            .buscarPorId(opcion.getIdOpcion())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la opcion indicada"));

            validarMismaRelacion(existente.getIdPregunta(), opcion.getIdPregunta());
        }

        validarBorrador(buscarVersion(idVersion));

        return opcionRepository.guardar(opcion);
    }

    private VersionFormulario buscarVersion(Long idVersion) {

        if (idVersion == null || idVersion <= 0) {
            throw new IllegalArgumentException("El ID de version debe ser positivo");
        }

        return versionRepository
                .buscarPorId(idVersion)
                .orElseThrow(
                        () ->
                                new NoSuchElementException(
                                        "No existe la version con ID: " + idVersion));
    }

    private ConfiguracionSubprueba buscarConfiguracion(Long idConfiguracion) {

        if (idConfiguracion == null || idConfiguracion <= 0) {
            throw new IllegalArgumentException("El ID de configuracion debe ser positivo");
        }

        return configuracionRepository
                .buscarPorId(idConfiguracion)
                .orElseThrow(
                        () -> new NoSuchElementException("No existe la configuracion indicada"));
    }

    private Pregunta buscarPregunta(Long idPregunta) {

        if (idPregunta == null || idPregunta <= 0) {
            throw new IllegalArgumentException("El ID de pregunta debe ser positivo");
        }

        return preguntaRepository
                .buscarPorId(idPregunta)
                .orElseThrow(() -> new NoSuchElementException("No existe la pregunta indicada"));
    }

    private void validarMismaRelacion(Long idExistente, Long idSolicitado) {

        if (!Objects.equals(idExistente, idSolicitado)) {
            throw new IllegalArgumentException("No se puede cambiar la relacion original");
        }
    }

    private void validarBorrador(VersionFormulario version) {

        if (version.getEstado() != EstadoVersionFormulario.BORRADOR) {

            throw new IllegalStateException(
                    "Solo se puede editar contenido " + "de una version en BORRADOR");
        }
    }
}
