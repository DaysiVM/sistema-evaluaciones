package com.universidad.tests.application.usecase.bfa;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.OpcionRespuesta;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.Test;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.model.bfa.ConfiguracionBfa;
import com.universidad.tests.domain.model.bfa.SubpruebaBfa;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.OpcionRespuestaRepository;
import com.universidad.tests.domain.repository.PreguntaRepository;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.domain.repository.TestRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga datos FICTICIOS del BFA para el perfil demo: el test BFA, sus 13 subpruebas oficiales
 * (orden y tiempos del Documento BFA, seccion 2.6) y una version con tres preguntas inventadas por
 * subprueba. Nunca contiene items reales del test.
 *
 * <p>Es idempotente: si el contenido ya existe no lo duplica, y si la version ya no esta en
 * BORRADOR no la modifica. No carga la clave ni los baremos (BFA-BE-39).
 *
 * <p>Escribe directamente en los repositorios y no pasa por los servicios de edicion, porque estos
 * exigen permisos de contenido que el arranque demo no tiene.
 */
@Service
public class CargarCatalogoDemoBfaUseCase {

    public static final String CODIGO_VERSION_DEMO = "DEMO-1";
    public static final int PREGUNTAS_POR_SUBPRUEBA = 3;
    static final String FUENTE_DEMO = "demo/datos-ficticios";
    static final String EVIDENCIA_DEMO =
            "Datos ficticios cargados automaticamente con el perfil demo";

    private final TestRepository testRepository;
    private final SubpruebaRepository subpruebaRepository;
    private final VersionFormularioRepository versionRepository;
    private final ConfiguracionSubpruebaRepository configuracionRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionRespuestaRepository opcionRepository;
    private final ConsultarConfiguracionBfaUseCase consultarConfiguracion;

    public CargarCatalogoDemoBfaUseCase(
            TestRepository testRepository,
            SubpruebaRepository subpruebaRepository,
            VersionFormularioRepository versionRepository,
            ConfiguracionSubpruebaRepository configuracionRepository,
            PreguntaRepository preguntaRepository,
            OpcionRespuestaRepository opcionRepository,
            ConsultarConfiguracionBfaUseCase consultarConfiguracion) {
        this.testRepository = testRepository;
        this.subpruebaRepository = subpruebaRepository;
        this.versionRepository = versionRepository;
        this.configuracionRepository = configuracionRepository;
        this.preguntaRepository = preguntaRepository;
        this.opcionRepository = opcionRepository;
        this.consultarConfiguracion = consultarConfiguracion;
    }

    /** Crea (si faltan) el test, las subpruebas y el contenido de la version demo en BORRADOR. */
    @Transactional
    public VersionFormulario cargarContenido() {
        Test test =
                testRepository
                        .buscarPorCodigo(ConfiguracionBfa.CODIGO_TEST)
                        .orElseGet(
                                () ->
                                        testRepository.guardar(
                                                new Test(
                                                        null,
                                                        ConfiguracionBfa.CODIGO_TEST,
                                                        "Bateria Factorial de Aptitudes",
                                                        "Datos ficticios del perfil demo",
                                                        true)));
        Long idTest = test.getIdTest();

        VersionFormulario version =
                versionRepository
                        .buscarPorTestYCodigo(idTest, CODIGO_VERSION_DEMO)
                        .orElseGet(
                                () ->
                                        versionRepository.guardar(
                                                new VersionFormulario(
                                                        null,
                                                        idTest,
                                                        CODIGO_VERSION_DEMO,
                                                        "Version ficticia para el perfil demo",
                                                        FUENTE_DEMO)));
        if (version.getEstado() != EstadoVersionFormulario.BORRADOR) {
            return version;
        }

        for (SubpruebaBfa oficial : SubpruebaBfa.values()) {
            Subprueba subprueba = obtenerSubprueba(idTest, oficial);
            ConfiguracionSubprueba configuracion =
                    obtenerConfiguracion(version.getIdVersionFormulario(), subprueba, oficial);
            cargarPreguntas(configuracion, oficial);
        }
        return version;
    }

    /**
     * Publica la version si sigue en BORRADOR y su configuracion coincide con el perfil oficial. Si
     * falla, la version queda en BORRADOR.
     */
    @Transactional
    public VersionFormulario publicar(Long idVersionFormulario) {
        VersionFormulario version =
                versionRepository
                        .buscarPorId(idVersionFormulario)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No existe la version con ID: "
                                                        + idVersionFormulario));
        if (version.getEstado() != EstadoVersionFormulario.BORRADOR) {
            return version;
        }
        consultarConfiguracion.porVersion(idVersionFormulario);
        version.publicar(EVIDENCIA_DEMO);
        return versionRepository.guardar(version);
    }

    private Subprueba obtenerSubprueba(Long idTest, SubpruebaBfa oficial) {
        return subpruebaRepository
                .buscarPorCodigo(idTest, oficial.getCodigo())
                .orElseGet(
                        () ->
                                subpruebaRepository.guardar(
                                        new Subprueba(
                                                null,
                                                idTest,
                                                oficial.getCodigo(),
                                                oficial.getNombre(),
                                                null,
                                                oficial.getFactor())));
    }

    private ConfiguracionSubprueba obtenerConfiguracion(
            Long idVersion, Subprueba subprueba, SubpruebaBfa oficial) {
        return configuracionRepository
                .buscarPorVersionYSubprueba(idVersion, subprueba.getIdSubprueba())
                .orElseGet(
                        () ->
                                configuracionRepository.guardar(
                                        new ConfiguracionSubprueba(
                                                null,
                                                idVersion,
                                                subprueba.getIdSubprueba(),
                                                oficial.getOrden(),
                                                oficial.getTiempoLimiteSegundos(),
                                                oficial.getTipoRespuesta(),
                                                "demo/instrucciones/" + oficial.getCodigo(),
                                                null,
                                                oficial.getMaximoEntradas())));
    }

    private void cargarPreguntas(ConfiguracionSubprueba configuracion, SubpruebaBfa oficial) {
        Long idConfiguracion = configuracion.getIdConfiguracionSubprueba();
        for (int n = 1; n <= PREGUNTAS_POR_SUBPRUEBA; n++) {
            String numero = String.valueOf(n);
            final int orden = n;
            Pregunta pregunta =
                    preguntaRepository
                            .buscarPorNumeroVisible(idConfiguracion, numero)
                            .orElseGet(
                                    () ->
                                            preguntaRepository.guardar(
                                                    new Pregunta(
                                                            null,
                                                            idConfiguracion,
                                                            numero,
                                                            orden,
                                                            enunciado(oficial, orden),
                                                            null)));
            if (oficial.getTipoRespuesta() == TipoRespuesta.SELECCION) {
                cargarOpciones(pregunta, alternativas(oficial));
            }
        }
    }

    private void cargarOpciones(Pregunta pregunta, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            String codigo = String.valueOf((char) ('A' + i));
            if (!opcionRepository.existePorCodigo(pregunta.getIdPregunta(), codigo)) {
                opcionRepository.guardar(
                        new OpcionRespuesta(
                                null,
                                pregunta.getIdPregunta(),
                                codigo,
                                i + 1,
                                "Opcion " + codigo + " (ficticia)",
                                null));
            }
        }
    }

    private static String enunciado(SubpruebaBfa oficial, int numero) {
        if (oficial.getTipoRespuesta() == TipoRespuesta.TEXTO_LIBRE) {
            return "[DEMO] Consigna ficticia " + numero + " de " + oficial.getCodigo();
        }
        return "[DEMO] Pregunta ficticia " + numero + " de " + oficial.getCodigo();
    }

    /** Cantidad de alternativas por subprueba segun el Documento BFA (E2). */
    static int alternativas(SubpruebaBfa oficial) {
        return switch (oficial) {
            case VOC1, VOC2, S1, S2, S1B -> 5;
            case R1, R2, N2_A, N2_B -> 4;
            case CV -> 3;
            case N1_A, N1_B -> 2;
            case FV -> 0;
        };
    }
}
