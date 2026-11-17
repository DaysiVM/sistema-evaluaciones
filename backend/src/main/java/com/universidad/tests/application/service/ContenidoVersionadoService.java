package com.universidad.tests.application.service;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.OpcionRespuesta;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.OpcionRespuestaRepository;
import com.universidad.tests.domain.repository.PreguntaRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContenidoVersionadoService {

    private final VersionFormularioRepository versionRepository;
    private final ConfiguracionSubpruebaRepository configuracionRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionRespuestaRepository opcionRepository;
    private final AutorizacionContenidoPort autorizacion;

    public ContenidoVersionadoService(
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

    @Transactional(readOnly = true)
    public FormularioContenido obtenerContenido(Long idVersionFormulario) {

        // 1. Validar el identificador
        if (idVersionFormulario == null || idVersionFormulario <= 0) {

            throw new IllegalArgumentException("El ID de la version debe ser positivo");
        }

        // 2. Verificar autorizacion antes de consultar contenido
        autorizacion.exigirPermisoConsultarContenido(idVersionFormulario);

        // 3. Recuperar la version del formulario
        VersionFormulario version =
                versionRepository
                        .buscarPorId(idVersionFormulario)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No existe la version con ID: "
                                                        + idVersionFormulario));

        // 4. Recuperar las configuraciones de la version
        List<ConfiguracionContenido> configuraciones =
                configuracionRepository.listarPorVersion(idVersionFormulario).stream()
                        .map(this::cargarConfiguracion)
                        .toList();

        // 5. Devolver el contenido completo
        return new FormularioContenido(version, configuraciones);
    }

    private ConfiguracionContenido cargarConfiguracion(ConfiguracionSubprueba configuracion) {

        // Recuperar las preguntas
        List<PreguntaContenido> preguntas =
                preguntaRepository
                        .listarPorConfiguracion(configuracion.getIdConfiguracionSubprueba())
                        .stream()
                        .map(this::cargarPregunta)
                        .toList();

        return new ConfiguracionContenido(configuracion, preguntas);
    }

    private PreguntaContenido cargarPregunta(Pregunta pregunta) {

        // Recuperar las opciones de respuesta
        List<OpcionRespuesta> opciones =
                opcionRepository.listarPorPregunta(pregunta.getIdPregunta());

        return new PreguntaContenido(pregunta, opciones);
    }

    // Contenedor principal del formulario
    public record FormularioContenido(
            VersionFormulario version, List<ConfiguracionContenido> configuraciones) {

        public FormularioContenido {
            configuraciones = List.copyOf(configuraciones);
        }
    }

    // Contenedor de cada configuracion
    public record ConfiguracionContenido(
            ConfiguracionSubprueba configuracion, List<PreguntaContenido> preguntas) {

        public ConfiguracionContenido {
            preguntas = List.copyOf(preguntas);
        }
    }

    // Contenedor de cada pregunta
    public record PreguntaContenido(Pregunta pregunta, List<OpcionRespuesta> opciones) {

        public PreguntaContenido {
            opciones = List.copyOf(opciones);
        }
    }
}
