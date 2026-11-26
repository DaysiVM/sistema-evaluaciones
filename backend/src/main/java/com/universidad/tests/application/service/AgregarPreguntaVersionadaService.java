package com.universidad.tests.application.service;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.PreguntaRepository;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import com.universidad.tests.dto.PreguntaCreada;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgregarPreguntaVersionadaService {

    private final VersionFormularioRepository versiones;
    private final SubpruebaRepository subpruebas;
    private final ConfiguracionSubpruebaRepository configuraciones;
    private final PreguntaRepository preguntas;
    private final AutorizacionContenidoPort autorizacion;
    private final EdicionContenidoVersionadoService edicion;

    public AgregarPreguntaVersionadaService(
            VersionFormularioRepository versiones,
            SubpruebaRepository subpruebas,
            ConfiguracionSubpruebaRepository configuraciones,
            PreguntaRepository preguntas,
            AutorizacionContenidoPort autorizacion,
            EdicionContenidoVersionadoService edicion) {
        this.versiones = versiones;
        this.subpruebas = subpruebas;
        this.configuraciones = configuraciones;
        this.preguntas = preguntas;
        this.autorizacion = autorizacion;
        this.edicion = edicion;
    }

    @Transactional
    public PreguntaCreada agregar(
            Long idVersion,
            String codigoSubprueba,
            String numeroVisible,
            int ordenPresentacion,
            String enunciado,
            String recursoRef) {
        if (idVersion == null
                || idVersion <= 0
                || codigoSubprueba == null
                || codigoSubprueba.isBlank()) {
            throw new IllegalArgumentException(
                    "La version y el codigo de subprueba son obligatorios");
        }
        autorizacion.exigirPermisoEditarContenido(idVersion);
        VersionFormulario version =
                versiones
                        .buscarPorId(idVersion)
                        .orElseThrow(() -> new NoSuchElementException("Version no encontrada"));
        if (version.getEstado() != EstadoVersionFormulario.BORRADOR) {
            throw new IllegalStateException("VERSION_NO_EDITABLE");
        }
        Subprueba subprueba =
                subpruebas
                        .buscarPorCodigo(version.getIdTest(), codigoSubprueba)
                        .orElseThrow(() -> new NoSuchElementException("Subprueba no encontrada"));
        ConfiguracionSubprueba configuracion =
                configuraciones
                        .buscarPorVersionYSubprueba(idVersion, subprueba.getIdSubprueba())
                        .orElseThrow(
                                () -> new NoSuchElementException("Configuracion no encontrada"));
        if (preguntas.existePorNumeroVisible(
                        configuracion.getIdConfiguracionSubprueba(), numeroVisible)
                || preguntas
                        .listarPorConfiguracion(configuracion.getIdConfiguracionSubprueba())
                        .stream()
                        .anyMatch(p -> p.getOrdenPresentacion() == ordenPresentacion)) {
            throw new PreguntaDuplicadaException();
        }
        Pregunta creada =
                edicion.guardarPregunta(
                        new Pregunta(
                                null,
                                configuracion.getIdConfiguracionSubprueba(),
                                numeroVisible,
                                ordenPresentacion,
                                enunciado,
                                recursoRef));
        return new PreguntaCreada(
                creada.getIdPregunta(),
                Integer.valueOf(creada.getNumeroVisible()),
                creada.getOrdenPresentacion(),
                creada.getEnunciado(),
                creada.getRecursoRef());
    }

    public static class PreguntaDuplicadaException extends RuntimeException {
        public PreguntaDuplicadaException() {
            super("PREGUNTA_DUPLICADA");
        }
    }
}
