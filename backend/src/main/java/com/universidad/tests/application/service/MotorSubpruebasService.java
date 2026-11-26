package com.universidad.tests.application.service;

import static com.universidad.tests.domain.exception.MotorSubpruebasException.Codigo.*;

import com.universidad.tests.domain.exception.EvaluacionNoEncontradaException;
import com.universidad.tests.domain.exception.MotorSubpruebasException;
import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.EstadoEvaluacion;
import com.universidad.tests.domain.model.EstadoSubprueba;
import com.universidad.tests.domain.model.Evaluacion;
import com.universidad.tests.domain.model.EvaluacionSubprueba;
import com.universidad.tests.domain.model.RespuestaEvaluacion;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.port.AutorizacionMotorSubpruebasPort;
import com.universidad.tests.domain.port.AutorizacionMotorSubpruebasPort.Permiso;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.EvaluacionRepository;
import com.universidad.tests.domain.repository.OpcionRespuestaRepository;
import com.universidad.tests.domain.repository.PreguntaRepository;
import com.universidad.tests.domain.repository.RespuestaEvaluacionRepository;
import com.universidad.tests.dto.request.RegistrarRespuestasRequest;
import com.universidad.tests.dto.response.AvanceSubpruebaResponse;
import com.universidad.tests.dto.response.PreguntasSubpruebaResponse;
import com.universidad.tests.dto.response.SubpruebaActivaResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MotorSubpruebasService {
    private final EvaluacionRepository evaluaciones;
    private final ConfiguracionSubpruebaRepository configuraciones;
    private final PreguntaRepository preguntas;
    private final OpcionRespuestaRepository opciones;
    private final RespuestaEvaluacionRepository respuestas;
    private final AutorizacionMotorSubpruebasPort autorizacion;
    private final AutorizacionService sesiones;
    private final RegistroAuditoria auditoria;
    private final Clock reloj;

    @Autowired
    public MotorSubpruebasService(
            EvaluacionRepository evaluaciones,
            ConfiguracionSubpruebaRepository configuraciones,
            PreguntaRepository preguntas,
            OpcionRespuestaRepository opciones,
            RespuestaEvaluacionRepository respuestas,
            AutorizacionMotorSubpruebasPort autorizacion,
            AutorizacionService sesiones,
            RegistroAuditoria auditoria) {
        this(
                evaluaciones,
                configuraciones,
                preguntas,
                opciones,
                respuestas,
                autorizacion,
                sesiones,
                auditoria,
                Clock.systemUTC());
    }

    public MotorSubpruebasService(
            EvaluacionRepository evaluaciones,
            ConfiguracionSubpruebaRepository configuraciones,
            PreguntaRepository preguntas,
            OpcionRespuestaRepository opciones,
            RespuestaEvaluacionRepository respuestas,
            AutorizacionMotorSubpruebasPort autorizacion,
            AutorizacionService sesiones,
            RegistroAuditoria auditoria,
            Clock reloj) {
        this.evaluaciones = evaluaciones;
        this.configuraciones = configuraciones;
        this.preguntas = preguntas;
        this.opciones = opciones;
        this.respuestas = respuestas;
        this.autorizacion = autorizacion;
        this.sesiones = sesiones;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    @Transactional
    public SubpruebaActivaResponse iniciar(Long id, String codigo) {
        Evaluacion evaluacion = obtenerEvaluacion(id, Permiso.SUBPRUEBA_INICIAR, true);
        EvaluacionSubprueba subprueba = buscarSubprueba(evaluacion, codigo);
        if (evaluacion.estado() != EstadoEvaluacion.EN_CURSO
                || subprueba.estado() == EstadoSubprueba.CERRADA) {
            throw new MotorSubpruebasException(
                    SUBPRUEBA_CERRADA, "La subprueba no se puede iniciar.");
        }

        Instant ahora = reloj.instant().truncatedTo(ChronoUnit.MICROS);
        if (subprueba.estado() == EstadoSubprueba.ACTIVA) {
            return MotorSubpruebasMapper.activa(subprueba, ahora);
        }
        if (evaluacion.subpruebaActiva().isPresent()) {
            throw new MotorSubpruebasException(
                    HAY_SUBPRUEBA_ACTIVA, "Ya existe una subprueba activa.");
        }
        if (evaluacion.subpruebas().stream()
                .anyMatch(
                        s ->
                                s.orden() < subprueba.orden()
                                        && s.estado() == EstadoSubprueba.PENDIENTE)) {
            throw new MotorSubpruebasException(
                    SUBPRUEBA_FUERA_DE_ORDEN, "Debe iniciar la siguiente subprueba pendiente.");
        }
        if (subprueba.esFluidezVerbal()
                && (subprueba.letraInicial() == null || subprueba.letraInicial().isBlank())) {
            throw new MotorSubpruebasException(
                    LETRA_NO_CONFIGURADA, "La letra inicial de FV no estÃ¡ configurada.");
        }

        if (subprueba.duracionSegundos() <= 0) {
            throw new MotorSubpruebasException(
                    ERROR_INTERNO, "La duraciÃ³n de la subprueba no estÃ¡ configurada.");
        }

        long idCuenta = sesiones.exigirSesion().idCuenta();
        EvaluacionSubprueba activa = subprueba.iniciar(ahora);
        evaluaciones.iniciarSubprueba(activa);
        auditoria.registrar(
                idCuenta,
                "INICIAR_SUBPRUEBA",
                "EVALUACION_SUBPRUEBA",
                activa.idEvaluacionSubprueba());
        return MotorSubpruebasMapper.activa(activa, ahora);
    }

    @Transactional(readOnly = true)
    public PreguntasSubpruebaResponse consultarPreguntas(Long id, String codigo) {
        Evaluacion evaluacion = obtenerEvaluacion(id, Permiso.ITEMS_LEER, false);
        EvaluacionSubprueba subprueba = buscarSubprueba(evaluacion, codigo);
        exigirActiva(evaluacion, subprueba);
        Instant ahora = reloj.instant();
        if (subprueba.vencio(ahora)) {
            throw new MotorSubpruebasException(
                    SUBPRUEBA_NO_ACTIVA, "El tiempo de la subprueba finalizÃ³.");
        }
        ConfiguracionSubprueba configuracion = configuracionAplicada(evaluacion, subprueba);
        List<RespuestaEvaluacion> guardadas = respuestas.listar(id, subprueba.codigo());
        if (subprueba.esFluidezVerbal()) {
            return new PreguntasSubpruebaResponse(
                    subprueba.codigo(),
                    subprueba.segundosRestantes(ahora),
                    List.of(),
                    guardadas.stream()
                            .filter(r -> r.idPregunta() == null)
                            .map(
                                    r ->
                                            new PreguntasSubpruebaResponse.Palabra(
                                                    r.ordenEntrada(), r.valorTexto()))
                            .toList());
        }

        Map<Long, Long> opcionesRegistradas = new HashMap<>();
        guardadas.stream()
                .filter(r -> r.idPregunta() != null && r.ordenEntrada() == 1)
                .forEach(r -> opcionesRegistradas.put(r.idPregunta(), r.idOpcion()));
        var contenido =
                preguntas
                        .listarPorConfiguracion(configuracion.getIdConfiguracionSubprueba())
                        .stream()
                        .map(
                                p ->
                                        MotorSubpruebasMapper.pregunta(
                                                p,
                                                opciones.listarPorPregunta(p.getIdPregunta()),
                                                opcionesRegistradas.get(p.getIdPregunta())))
                        .toList();
        return new PreguntasSubpruebaResponse(
                subprueba.codigo(), subprueba.segundosRestantes(ahora), contenido, null);
    }

    @Transactional
    public AvanceSubpruebaResponse registrarRespuestas(
            Long id, String codigo, RegistrarRespuestasRequest solicitud) {
        Instant recepcion = reloj.instant();
        Evaluacion evaluacion = obtenerEvaluacion(id, Permiso.RESPUESTA_ESCRIBIR, true);
        EvaluacionSubprueba subprueba = buscarSubprueba(evaluacion, codigo);
        // GEN-38-BE debe usar el mismo bloqueo y distinguir cierres anticipados del cierre
        // temporal.
        boolean cierreAnticipado =
                subprueba.fechaCierre() != null
                        && subprueba.fechaFinLimite() != null
                        && subprueba.fechaCierre().isBefore(subprueba.fechaFinLimite());
        if (subprueba.vencio(recepcion) && !cierreAnticipado) {
            throw new MotorSubpruebasException(
                    TIEMPO_AGOTADO, "La respuesta llegÃ³ despuÃ©s del plazo.");
        }
        exigirActiva(evaluacion, subprueba);
        ConfiguracionSubprueba configuracion = configuracionAplicada(evaluacion, subprueba);
        List<RespuestaEvaluacion> cambios;
        if (subprueba.esFluidezVerbal()) {
            cambios = validarPalabras(subprueba, solicitud, recepcion);
        } else {
            cambios = validarSeleccion(subprueba, configuracion, solicitud, recepcion);
        }
        cambios.forEach(respuestas::registrar);
        return new AvanceSubpruebaResponse(
                subprueba.codigo(),
                cambios.size(),
                respuestas.contar(id, subprueba.codigo()),
                subprueba.segundosRestantes(reloj.instant()));
    }

    private List<RespuestaEvaluacion> validarSeleccion(
            EvaluacionSubprueba subprueba,
            ConfiguracionSubprueba configuracion,
            RegistrarRespuestasRequest solicitud,
            Instant ahora) {
        if (configuracion.getTipoRespuesta() != TipoRespuesta.SELECCION
                || solicitud == null
                || solicitud.palabras() != null
                || solicitud.respuestas() == null
                || solicitud.respuestas().isEmpty()) {
            throw new MotorSubpruebasException(
                    SOLICITUD_INVALIDA, "EnvÃ­e una lista de respuestas de selecciÃ³n.");
        }
        Set<Long> incluidas = new HashSet<>();
        return solicitud.respuestas().stream()
                .map(
                        item -> {
                            if (item == null
                                    || item.preguntaId() == null
                                    || item.opcion() == null
                                    || item.opcion().isBlank()
                                    || !incluidas.add(item.preguntaId())) {
                                throw new MotorSubpruebasException(
                                        SOLICITUD_INVALIDA,
                                        "Cada respuesta requiere una pregunta Ãºnica y una opciÃ³n.");
                            }
                            preguntas
                                    .buscarPorId(item.preguntaId())
                                    .filter(
                                            p ->
                                                    p.getIdConfiguracionSubprueba()
                                                            .equals(
                                                                    subprueba
                                                                            .idConfiguracionSubprueba()))
                                    .orElseThrow(
                                            () ->
                                                    new MotorSubpruebasException(
                                                            RESPUESTA_INVALIDA,
                                                            "La pregunta no pertenece a la configuraciÃ³n aplicada."));
                            var opcion =
                                    opciones.buscarPorCodigo(item.preguntaId(), item.opcion())
                                            .orElseThrow(
                                                    () ->
                                                            new MotorSubpruebasException(
                                                                    RESPUESTA_INVALIDA,
                                                                    "La opciÃ³n no pertenece a la pregunta."));
                            return new RespuestaEvaluacion(
                                    null,
                                    subprueba.idEvaluacion(),
                                    subprueba.codigo(),
                                    item.preguntaId(),
                                    opcion.getIdOpcion(),
                                    null,
                                    1,
                                    ahora);
                        })
                .toList();
    }

    private List<RespuestaEvaluacion> validarPalabras(
            EvaluacionSubprueba subprueba, RegistrarRespuestasRequest solicitud, Instant ahora) {
        if (solicitud == null
                || solicitud.respuestas() != null
                || solicitud.palabras() == null
                || solicitud.palabras().isEmpty()
                || solicitud.palabras().size() > subprueba.maximoEntradas()) {
            throw new MotorSubpruebasException(
                    SOLICITUD_INVALIDA, "EnvÃ­e una lista de palabras dentro del mÃ¡ximo de FV.");
        }
        Set<Integer> ordenes = new HashSet<>();
        return solicitud.palabras().stream()
                .map(
                        palabra -> {
                            if (palabra == null
                                    || palabra.ordenEntrada() == null
                                    || palabra.ordenEntrada() < 1
                                    || palabra.ordenEntrada() > subprueba.maximoEntradas()
                                    || !ordenes.add(palabra.ordenEntrada())
                                    || palabra.texto() == null
                                    || palabra.texto().isBlank()) {
                                throw new MotorSubpruebasException(
                                        SOLICITUD_INVALIDA,
                                        "FV requiere texto no vacÃ­o y posiciones Ãºnicas dentro del mÃ¡ximo configurado.");
                            }
                            if (palabra.texto().indexOf('\0') >= 0) {
                                throw new MotorSubpruebasException(
                                        SOLICITUD_INVALIDA,
                                        "El texto de FV no puede contener el carÃ¡cter nulo.");
                            }
                            return new RespuestaEvaluacion(
                                    null,
                                    subprueba.idEvaluacion(),
                                    subprueba.codigo(),
                                    null,
                                    null,
                                    palabra.texto(),
                                    palabra.ordenEntrada(),
                                    ahora);
                        })
                .toList();
    }

    private Evaluacion obtenerEvaluacion(Long id, Permiso permiso, boolean bloquear) {
        autorizacion.exigirPermiso(permiso);
        Optional<Evaluacion> resultado =
                bloquear
                        ? evaluaciones.buscarPorIdParaActualizar(id)
                        : evaluaciones.buscarPorId(id);
        Evaluacion evaluacion =
                resultado.orElseThrow(() -> new EvaluacionNoEncontradaException(id));
        autorizacion.exigirAcceso(permiso, id, evaluacion.idSujeto());
        return evaluacion;
    }

    private EvaluacionSubprueba buscarSubprueba(Evaluacion evaluacion, String codigo) {
        return evaluacion.subpruebas().stream()
                .filter(s -> s.codigo().equals(codigo))
                .findFirst()
                .orElseThrow(
                        () ->
                                new MotorSubpruebasException(
                                        SUBPRUEBA_NO_ENCONTRADA, "Subprueba no encontrada."));
    }

    private ConfiguracionSubprueba configuracionAplicada(
            Evaluacion evaluacion, EvaluacionSubprueba subprueba) {
        return configuraciones
                .buscarPorId(subprueba.idConfiguracionSubprueba())
                .filter(c -> c.getIdVersionFormulario().equals(evaluacion.idVersionFormulario()))
                .orElseThrow(
                        () ->
                                new MotorSubpruebasException(
                                        ERROR_INTERNO,
                                        "La configuraciÃ³n aplicada no estÃ¡ disponible."));
    }

    private void exigirActiva(Evaluacion evaluacion, EvaluacionSubprueba subprueba) {
        if (evaluacion.estado() != EstadoEvaluacion.EN_CURSO
                || subprueba.estado() != EstadoSubprueba.ACTIVA) {
            throw new MotorSubpruebasException(
                    SUBPRUEBA_NO_ACTIVA, "La subprueba no estÃ¡ activa.");
        }
    }
}
