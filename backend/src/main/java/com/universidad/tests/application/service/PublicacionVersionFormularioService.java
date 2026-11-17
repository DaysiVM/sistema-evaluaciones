package com.universidad.tests.application.service;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicacionVersionFormularioService {

    private final VersionFormularioRepository versionRepository;
    private final ConfiguracionSubpruebaRepository configuracionRepository;
    private final SubpruebaRepository subpruebaRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionRespuestaRepository opcionRepository;
    private final AutorizacionContenidoPort autorizacion;

    public PublicacionVersionFormularioService(
            VersionFormularioRepository versionRepository,
            ConfiguracionSubpruebaRepository configuracionRepository,
            SubpruebaRepository subpruebaRepository,
            PreguntaRepository preguntaRepository,
            OpcionRespuestaRepository opcionRepository,
            AutorizacionContenidoPort autorizacion) {

        this.versionRepository = versionRepository;
        this.configuracionRepository = configuracionRepository;
        this.subpruebaRepository = subpruebaRepository;
        this.preguntaRepository = preguntaRepository;
        this.opcionRepository = opcionRepository;
        this.autorizacion = autorizacion;
    }

    @Transactional
    public VersionFormulario publicar(Long idVersionFormulario, String evidenciaValidacion) {

        validarId(idVersionFormulario);

        // Exigir permiso antes de publicar.
        autorizacion.exigirPermisoPublicar(idVersionFormulario);

        VersionFormulario version = buscarVersion(idVersionFormulario);

        if (version.getEstado() != EstadoVersionFormulario.BORRADOR) {

            throw new IllegalStateException("Solo se puede publicar una version en BORRADOR");
        }

        if (evidenciaValidacion == null || evidenciaValidacion.isBlank()) {

            throw new IllegalArgumentException("La evidencia de validacion es obligatoria");
        }

        // Comprobar que el formulario tenga contenido valido.
        validarContenido(version);

        // Registrar el cambio de estado.
        version.publicar(evidenciaValidacion);

        return versionRepository.guardar(version);
    }

    @Transactional
    public VersionFormulario retirar(Long idVersionFormulario) {

        validarId(idVersionFormulario);

        // Exigir permiso antes de retirar.
        autorizacion.exigirPermisoRetirar(idVersionFormulario);

        VersionFormulario version = buscarVersion(idVersionFormulario);

        if (version.getEstado() != EstadoVersionFormulario.PUBLICADA) {

            throw new IllegalStateException("Solo se puede retirar una version PUBLICADA");
        }

        version.retirar();

        return versionRepository.guardar(version);
    }

    private void validarContenido(VersionFormulario version) {

        Long idVersion = version.getIdVersionFormulario();

        List<ConfiguracionSubprueba> configuraciones =
                configuracionRepository.listarPorVersion(idVersion);

        if (configuraciones.isEmpty()) {

            throw new IllegalStateException("No se puede publicar una version sin subpruebas");
        }

        for (ConfiguracionSubprueba configuracion : configuraciones) {

            Subprueba subprueba =
                    subpruebaRepository
                            .buscarPorId(configuracion.getIdSubprueba())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe una subprueba de la configuracion"));

            // Comprobar que pertenece al mismo test.
            if (!Objects.equals(subprueba.getIdTest(), version.getIdTest())) {

                throw new IllegalStateException("Una subprueba pertenece a otro test");
            }

            // Recuperar preguntas de la configuracion.
            List<Pregunta> preguntas =
                    preguntaRepository.listarPorConfiguracion(
                            configuracion.getIdConfiguracionSubprueba());

            if (preguntas.isEmpty()) {

                throw new IllegalStateException(
                        "La subprueba " + subprueba.getCodigo() + " no contiene preguntas");
            }

            // Las preguntas de seleccion necesitan opciones.
            if (configuracion.getTipoRespuesta() == TipoRespuesta.SELECCION) {

                for (Pregunta pregunta : preguntas) {

                    if (opcionRepository.listarPorPregunta(pregunta.getIdPregunta()).isEmpty()) {

                        throw new IllegalStateException(
                                "La pregunta "
                                        + pregunta.getNumeroVisible()
                                        + " no contiene opciones");
                    }
                }
            }
        }
    }

    private VersionFormulario buscarVersion(Long idVersionFormulario) {

        return versionRepository
                .buscarPorId(idVersionFormulario)
                .orElseThrow(
                        () ->
                                new NoSuchElementException(
                                        "No existe la version con ID: " + idVersionFormulario));
    }

    private void validarId(Long idVersionFormulario) {

        if (idVersionFormulario == null || idVersionFormulario <= 0) {

            throw new IllegalArgumentException("El ID de version debe ser positivo");
        }
    }
}
