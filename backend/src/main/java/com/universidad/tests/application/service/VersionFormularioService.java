package com.universidad.tests.application.service;

import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.port.AutorizacionContenidoPort;
import com.universidad.tests.domain.repository.TestRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VersionFormularioService {

    private final VersionFormularioRepository versionRepository;
    private final TestRepository testRepository;
    private final AutorizacionContenidoPort autorizacion;

    public VersionFormularioService(
            VersionFormularioRepository versionRepository,
            TestRepository testRepository,
            AutorizacionContenidoPort autorizacion) {

        this.versionRepository = versionRepository;
        this.testRepository = testRepository;
        this.autorizacion = autorizacion;
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

            throw new IllegalArgumentException(
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
}
