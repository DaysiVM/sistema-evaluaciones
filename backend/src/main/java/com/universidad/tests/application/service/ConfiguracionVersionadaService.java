package com.universidad.tests.application.service;

import com.universidad.tests.domain.model.ConfiguracionSubprueba;
import com.universidad.tests.domain.model.EstadoVersionFormulario;
import com.universidad.tests.domain.model.Subprueba;
import com.universidad.tests.domain.model.TipoRespuesta;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionVersionadaService {
    private final VersionFormularioRepository versiones;
    private final SubpruebaRepository subpruebas;
    private final ConfiguracionSubpruebaRepository configuraciones;
    private final AutorizacionContenidoPort autorizacion;
    private final EdicionContenidoVersionadoService edicion;

    public ConfiguracionVersionadaService(
            VersionFormularioRepository versiones,
            SubpruebaRepository subpruebas,
            ConfiguracionSubpruebaRepository configuraciones,
            AutorizacionContenidoPort autorizacion,
            EdicionContenidoVersionadoService edicion) {
        this.versiones = versiones;
        this.subpruebas = subpruebas;
        this.configuraciones = configuraciones;
        this.autorizacion = autorizacion;
        this.edicion = edicion;
    }

    @Transactional
    public ConfiguracionSubprueba guardar(
            Long idVersion,
            String codigo,
            int orden,
            Integer tiempoLimiteSegundos,
            String tipoRespuestaTexto,
            Integer opciones,
            Integer maximoEntradas,
            String instrucciones,
            String practicaJson) {
        if (idVersion == null || idVersion <= 0 || codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("La version y el codigo son obligatorios");
        }
        TipoRespuesta tipoRespuesta = TipoRespuesta.valueOf(tipoRespuestaTexto);
        autorizacion.exigirPermisoConfigurarSubprueba(idVersion);
        VersionFormulario version =
                versiones
                        .buscarPorId(idVersion)
                        .orElseThrow(() -> new NoSuchElementException("Version no encontrada"));
        if (version.getEstado() != EstadoVersionFormulario.BORRADOR) {
            throw new IllegalStateException("VERSION_NO_EDITABLE");
        }
        Subprueba subprueba =
                subpruebas
                        .buscarPorCodigo(version.getIdTest(), codigo)
                        .orElseThrow(() -> new NoSuchElementException("Subprueba no encontrada"));
        if ("FV".equalsIgnoreCase(codigo) && tipoRespuesta != TipoRespuesta.TEXTO_LIBRE) {
            throw new IllegalArgumentException("Fluidez Verbal utiliza TEXTO_LIBRE");
        }
        ConfiguracionSubprueba anterior =
                configuraciones
                        .buscarPorVersionYSubprueba(idVersion, subprueba.getIdSubprueba())
                        .orElse(null);
        boolean ordenOcupado =
                configuraciones.listarPorVersion(idVersion).stream()
                        .anyMatch(
                                c ->
                                        c.getOrdenAplicacion() == orden
                                                && (anterior == null
                                                        || !Objects.equals(
                                                                c.getIdConfiguracionSubprueba(),
                                                                anterior
                                                                        .getIdConfiguracionSubprueba())));
        if (ordenOcupado) {
            throw new IllegalArgumentException("El orden ya pertenece a otra subprueba");
        }
        ConfiguracionSubprueba nueva =
                new ConfiguracionSubprueba(
                        anterior == null ? null : anterior.getIdConfiguracionSubprueba(),
                        idVersion,
                        subprueba.getIdSubprueba(),
                        orden,
                        tiempoLimiteSegundos,
                        tipoRespuesta,
                        instrucciones,
                        practicaJson,
                        maximoEntradas,
                        opciones);
        return edicion.guardarConfiguracionSubprueba(nueva);
    }
}
