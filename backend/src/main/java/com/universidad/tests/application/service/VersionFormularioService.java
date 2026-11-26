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
import com.universidad.tests.domain.repository.TestRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VersionFormularioService {

    private final VersionFormularioRepository versionRepository;
    private final TestRepository testRepository;
    private final AutorizacionContenidoPort autorizacion;
    private final ConfiguracionSubpruebaRepository configuraciones;
    private final PreguntaRepository preguntas;
    private final OpcionRespuestaRepository opciones;
    private final EdicionContenidoVersionadoService edicion;

    public VersionFormularioService(
            VersionFormularioRepository versionRepository,
            TestRepository testRepository,
            AutorizacionContenidoPort autorizacion,
            ConfiguracionSubpruebaRepository configuraciones,
            PreguntaRepository preguntas,
            OpcionRespuestaRepository opciones,
            EdicionContenidoVersionadoService edicion) {

        this.versionRepository = versionRepository;
        this.testRepository = testRepository;
        this.autorizacion = autorizacion;
        this.configuraciones = configuraciones;
        this.preguntas = preguntas;
        this.opciones = opciones;
        this.edicion = edicion;
    }

    @Transactional
    public VersionFormulario crearBorradorDesdeUltimaPublicada(
            Long idTest, String codigoVersion, String descripcion, String fuenteRef) {
        VersionFormulario borrador = crearBorrador(idTest, codigoVersion, descripcion, fuenteRef);
        VersionFormulario origen =
                versionRepository.listarPorTest(idTest).stream()
                        .filter(v -> v.getEstado() == EstadoVersionFormulario.PUBLICADA)
                        .max(Comparator.comparing(VersionFormulario::getPublicadaEn))
                        .orElse(null);
        if (origen == null) {
            return borrador;
        }
        // E25 ya autorizo la creacion; la copia interna no expone el contenido del origen.
        for (ConfiguracionSubprueba anterior :
                configuraciones.listarPorVersion(origen.getIdVersionFormulario())) {
            ConfiguracionSubprueba nueva =
                    edicion.guardarConfiguracion(
                            new ConfiguracionSubprueba(
                                    null,
                                    borrador.getIdVersionFormulario(),
                                    anterior.getIdSubprueba(),
                                    anterior.getOrdenAplicacion(),
                                    anterior.getTiempoLimiteSegundos(),
                                    anterior.getTipoRespuesta(),
                                    anterior.getInstruccionesRef(),
                                    anterior.getPracticaRef(),
                                    anterior.getMaximoEntradas(),
                                    anterior.getOpcionesEsperadas()));
            for (Pregunta pregunta :
                    preguntas.listarPorConfiguracion(anterior.getIdConfiguracionSubprueba())) {
                Pregunta copia =
                        edicion.guardarPregunta(
                                new Pregunta(
                                        null,
                                        nueva.getIdConfiguracionSubprueba(),
                                        pregunta.getNumeroVisible(),
                                        pregunta.getOrdenPresentacion(),
                                        pregunta.getEnunciado(),
                                        pregunta.getRecursoRef()));
                for (OpcionRespuesta opcion :
                        opciones.listarPorPregunta(pregunta.getIdPregunta())) {
                    edicion.guardarOpcion(
                            new OpcionRespuesta(
                                    null,
                                    copia.getIdPregunta(),
                                    opcion.getCodigo(),
                                    opcion.getOrdenPresentacion(),
                                    opcion.getTexto(),
                                    opcion.getRecursoRef()));
                }
            }
        }
        return borrador;
    }

    @Transactional
    public VersionFormulario crearBorrador(
            Long idTest, String codigoVersion, String descripcion, String fuenteRef) {

        if (idTest == null || idTest <= 0) {
            throw new IllegalArgumentException("El ID del test debe ser positivo");
        }

        // Comprobar autorizacion antes de crear la version
        autorizacion.exigirPermisoCrearVersion(idTest);

        if (codigoVersion == null || codigoVersion.isBlank()) {
            throw new IllegalArgumentException("El codigo de version es obligatorio");
        }

        if (fuenteRef == null || fuenteRef.isBlank()) {
            throw new IllegalArgumentException("La referencia de la fuente es obligatoria");
        }

        testRepository
                .buscarPorId(idTest)
                .orElseThrow(
                        () -> new NoSuchElementException("No existe el test con ID: " + idTest));

        if (versionRepository.existePorTestYCodigo(idTest, codigoVersion)) {

            throw new VersionDuplicadaException(
                    "Ya existe la version " + codigoVersion + " para el test " + idTest);
        }

        VersionFormulario nuevaVersion =
                new VersionFormulario(null, idTest, codigoVersion, descripcion, fuenteRef);

        return versionRepository.guardar(nuevaVersion);
    }

    @Transactional(readOnly = true)
    public VersionFormulario buscarPorId(Long idVersionFormulario) {

        if (idVersionFormulario == null || idVersionFormulario <= 0) {
            throw new IllegalArgumentException("El ID de la version debe ser positivo");
        }

        autorizacion.exigirPermisoConsultarContenido(idVersionFormulario);

        return versionRepository
                .buscarPorId(idVersionFormulario)
                .orElseThrow(
                        () ->
                                new NoSuchElementException(
                                        "No existe la version con ID: " + idVersionFormulario));
    }

    @Transactional(readOnly = true)
    public List<VersionFormulario> listarPorTest(Long idTest) {

        if (idTest == null || idTest <= 0) {
            throw new IllegalArgumentException("El ID del test debe ser positivo");
        }

        testRepository
                .buscarPorId(idTest)
                .orElseThrow(
                        () -> new NoSuchElementException("No existe el test con ID: " + idTest));

        List<VersionFormulario> versiones = versionRepository.listarPorTest(idTest);

        // Comprobar acceso a cada version antes de devolver la lista
        for (VersionFormulario version : versiones) {
            autorizacion.exigirPermisoConsultarContenido(version.getIdVersionFormulario());
        }

        return versiones;
    }

    @Transactional(readOnly = true)
    public VersionFormulario buscarPorTestYCodigo(Long idTest, String codigoVersion) {

        VersionFormulario version =
                versionRepository
                        .buscarPorTestYCodigo(idTest, codigoVersion)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No existe la version "
                                                        + codigoVersion
                                                        + " para el test "
                                                        + idTest));

        autorizacion.exigirPermisoConsultarContenido(version.getIdVersionFormulario());

        return version;
    }

    public static class VersionDuplicadaException extends IllegalStateException {
        public VersionDuplicadaException(String mensaje) {
            super(mensaje);
        }
    }
}
