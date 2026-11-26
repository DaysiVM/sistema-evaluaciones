package com.universidad.tests.application.usecase.bfa;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.OpcionRespuesta;
import com.universidad.tests.domain.model.Pregunta;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.Test;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.OpcionRespuestaRepository;
import com.universidad.tests.domain.repository.PreguntaRepository;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.domain.repository.TestRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Repositorios en memoria para probar el cargador demo del BFA sin base de datos. */
public final class ReposEnMemoria {

    private final AtomicLong secuencia = new AtomicLong(1);

    public final Tests tests = new Tests();
    public final Subpruebas subpruebas = new Subpruebas();
    public final Versiones versiones = new Versiones();
    public final Configuraciones configuraciones = new Configuraciones();
    public final Preguntas preguntas = new Preguntas();
    public final Opciones opciones = new Opciones();

    private long siguiente(Long id) {
        return id != null ? id : secuencia.getAndIncrement();
    }

    public final class Tests implements TestRepository {
        public final Map<Long, Test> datos = new LinkedHashMap<>();

        @Override
        public Optional<Test> buscarPorId(Long idTest) {
            return Optional.ofNullable(datos.get(idTest));
        }

        @Override
        public Optional<Test> buscarPorCodigo(String codigo) {
            return datos.values().stream().filter(t -> t.getCodigo().equals(codigo)).findFirst();
        }

        @Override
        public List<Test> listarActivos() {
            return new ArrayList<>(datos.values());
        }

        @Override
        public boolean existePorCodigo(String codigo) {
            return buscarPorCodigo(codigo).isPresent();
        }

        @Override
        public Test guardar(Test t) {
            Test nuevo =
                    new Test(
                            siguiente(t.getIdTest()),
                            t.getCodigo(),
                            t.getNombre(),
                            t.getDescripcion(),
                            t.isActivo());
            datos.put(nuevo.getIdTest(), nuevo);
            return nuevo;
        }
    }

    public final class Subpruebas implements SubpruebaRepository {
        public final Map<Long, Subprueba> datos = new LinkedHashMap<>();

        @Override
        public Optional<Subprueba> buscarPorId(Long idSubprueba) {
            return Optional.ofNullable(datos.get(idSubprueba));
        }

        @Override
        public Optional<Subprueba> buscarPorCodigo(Long idTest, String codigo) {
            return datos.values().stream()
                    .filter(s -> s.getIdTest().equals(idTest) && s.getCodigo().equals(codigo))
                    .findFirst();
        }

        @Override
        public List<Subprueba> listarPorTest(Long idTest) {
            return datos.values().stream().filter(s -> s.getIdTest().equals(idTest)).toList();
        }

        @Override
        public boolean existePorCodigo(Long idTest, String codigo) {
            return buscarPorCodigo(idTest, codigo).isPresent();
        }

        @Override
        public Subprueba guardar(Subprueba s) {
            Subprueba nueva =
                    new Subprueba(
                            siguiente(s.getIdSubprueba()),
                            s.getIdTest(),
                            s.getCodigo(),
                            s.getNombre(),
                            s.getDescripcion(),
                            s.getFactor());
            datos.put(nueva.getIdSubprueba(), nueva);
            return nueva;
        }
    }

    public final class Versiones implements VersionFormularioRepository {
        public final Map<Long, VersionFormulario> datos = new LinkedHashMap<>();

        @Override
        public Optional<VersionFormulario> buscarPorId(Long idVersionFormulario) {
            return Optional.ofNullable(datos.get(idVersionFormulario));
        }

        @Override
        public List<VersionFormulario> listarPorTest(Long idTest) {
            return datos.values().stream().filter(v -> v.getIdTest().equals(idTest)).toList();
        }

        @Override
        public Optional<VersionFormulario> buscarPorTestYCodigo(Long idTest, String codigo) {
            return datos.values().stream()
                    .filter(
                            v ->
                                    v.getIdTest().equals(idTest)
                                            && v.getCodigoVersion().equals(codigo))
                    .findFirst();
        }

        @Override
        public boolean existePorTestYCodigo(Long idTest, String codigoVersion) {
            return buscarPorTestYCodigo(idTest, codigoVersion).isPresent();
        }

        @Override
        public VersionFormulario guardar(VersionFormulario v) {
            VersionFormulario guardada =
                    VersionFormulario.restaurar(
                            siguiente(v.getIdVersionFormulario()),
                            v.getIdTest(),
                            v.getCodigoVersion(),
                            v.getDescripcion(),
                            v.getFuenteRef(),
                            v.getEstado(),
                            v.getPublicadaEn(),
                            v.getEvidenciaValidacion());
            datos.put(guardada.getIdVersionFormulario(), guardada);
            return guardada;
        }
    }

    public final class Configuraciones implements ConfiguracionSubpruebaRepository {
        public final Map<Long, ConfiguracionSubprueba> datos = new LinkedHashMap<>();

        @Override
        public Optional<ConfiguracionSubprueba> buscarPorId(Long id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public List<ConfiguracionSubprueba> listarPorVersion(Long idVersionFormulario) {
            return datos.values().stream()
                    .filter(c -> c.getIdVersionFormulario().equals(idVersionFormulario))
                    .toList();
        }

        @Override
        public Optional<ConfiguracionSubprueba> buscarPorVersionYSubprueba(
                Long idVersionFormulario, Long idSubprueba) {
            return listarPorVersion(idVersionFormulario).stream()
                    .filter(c -> c.getIdSubprueba().equals(idSubprueba))
                    .findFirst();
        }

        @Override
        public boolean existePorVersionYSubprueba(Long idVersionFormulario, Long idSubprueba) {
            return buscarPorVersionYSubprueba(idVersionFormulario, idSubprueba).isPresent();
        }

        @Override
        public ConfiguracionSubprueba guardar(ConfiguracionSubprueba c) {
            ConfiguracionSubprueba nueva =
                    new ConfiguracionSubprueba(
                            siguiente(c.getIdConfiguracionSubprueba()),
                            c.getIdVersionFormulario(),
                            c.getIdSubprueba(),
                            c.getOrdenAplicacion(),
                            c.getTiempoLimiteSegundos(),
                            c.getTipoRespuesta(),
                            c.getInstruccionesRef(),
                            c.getPracticaRef(),
                            c.getMaximoEntradas());
            datos.put(nueva.getIdConfiguracionSubprueba(), nueva);
            return nueva;
        }
    }

    public final class Preguntas implements PreguntaRepository {
        public final Map<Long, Pregunta> datos = new LinkedHashMap<>();

        @Override
        public Optional<Pregunta> buscarPorId(Long idPregunta) {
            return Optional.ofNullable(datos.get(idPregunta));
        }

        @Override
        public List<Pregunta> listarPorConfiguracion(Long idConfiguracionSubprueba) {
            return datos.values().stream()
                    .filter(p -> p.getIdConfiguracionSubprueba().equals(idConfiguracionSubprueba))
                    .toList();
        }

        @Override
        public int contarPorConfiguracion(Long idConfiguracionSubprueba) {
            return listarPorConfiguracion(idConfiguracionSubprueba).size();
        }

        @Override
        public Optional<Pregunta> buscarPorNumeroVisible(
                Long idConfiguracionSubprueba, String numeroVisible) {
            return listarPorConfiguracion(idConfiguracionSubprueba).stream()
                    .filter(p -> p.getNumeroVisible().equals(numeroVisible))
                    .findFirst();
        }

        @Override
        public boolean existePorNumeroVisible(Long idConfiguracionSubprueba, String numeroVisible) {
            return buscarPorNumeroVisible(idConfiguracionSubprueba, numeroVisible).isPresent();
        }

        @Override
        public Pregunta guardar(Pregunta p) {
            Pregunta nueva =
                    new Pregunta(
                            siguiente(p.getIdPregunta()),
                            p.getIdConfiguracionSubprueba(),
                            p.getNumeroVisible(),
                            p.getOrdenPresentacion(),
                            p.getEnunciado(),
                            p.getRecursoRef());
            datos.put(nueva.getIdPregunta(), nueva);
            return nueva;
        }
    }

    public final class Opciones implements OpcionRespuestaRepository {
        public final Map<Long, OpcionRespuesta> datos = new LinkedHashMap<>();

        @Override
        public Optional<OpcionRespuesta> buscarPorId(Long idOpcion) {
            return Optional.ofNullable(datos.get(idOpcion));
        }

        @Override
        public List<OpcionRespuesta> listarPorPregunta(Long idPregunta) {
            return datos.values().stream()
                    .filter(o -> o.getIdPregunta().equals(idPregunta))
                    .toList();
        }

        @Override
        public List<Long> contarPorPreguntaDeConfiguracion(Long idConfiguracionSubprueba) {
            return preguntas.listarPorConfiguracion(idConfiguracionSubprueba).stream()
                    .map(p -> (long) listarPorPregunta(p.getIdPregunta()).size())
                    .toList();
        }

        @Override
        public Optional<OpcionRespuesta> buscarPorCodigo(Long idPregunta, String codigo) {
            return listarPorPregunta(idPregunta).stream()
                    .filter(o -> o.getCodigo().equals(codigo))
                    .findFirst();
        }

        @Override
        public boolean existePorCodigo(Long idPregunta, String codigo) {
            return buscarPorCodigo(idPregunta, codigo).isPresent();
        }

        @Override
        public OpcionRespuesta guardar(OpcionRespuesta o) {
            OpcionRespuesta nueva =
                    new OpcionRespuesta(
                            siguiente(o.getIdOpcion()),
                            o.getIdPregunta(),
                            o.getCodigo(),
                            o.getOrdenPresentacion(),
                            o.getTexto(),
                            o.getRecursoRef());
            datos.put(nueva.getIdOpcion(), nueva);
            return nueva;
        }
    }
}
