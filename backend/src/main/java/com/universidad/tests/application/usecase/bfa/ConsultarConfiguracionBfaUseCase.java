package com.universidad.tests.application.usecase.bfa;

import com.universidad.tests.domain.model.Test;
import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.model.bfa.ConfiguracionBfa;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.domain.repository.TestRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Entrega la configuracion validada de una edicion del BFA a partir del catalogo versionado.
 *
 * <p>Solo lee el catalogo de GEN-07: no lo carga. Mientras el test BFA, sus 13 subpruebas y la
 * configuracion de la edicion no existan en la base, responde "Falta la subprueba ...". La carga se
 * hace con los endpoints de contenido versionado (E25 a E28, PR #65 a #68) o con una migracion de
 * datos; quien la realiza esta pendiente de decision.
 */
@Service
public class ConsultarConfiguracionBfaUseCase {

    private final TestRepository testRepository;
    private final VersionFormularioRepository versionRepository;
    private final SubpruebaRepository subpruebaRepository;
    private final ConfiguracionSubpruebaRepository configuracionRepository;

    public ConsultarConfiguracionBfaUseCase(
            TestRepository testRepository,
            VersionFormularioRepository versionRepository,
            SubpruebaRepository subpruebaRepository,
            ConfiguracionSubpruebaRepository configuracionRepository) {
        this.testRepository = testRepository;
        this.versionRepository = versionRepository;
        this.subpruebaRepository = subpruebaRepository;
        this.configuracionRepository = configuracionRepository;
    }

    @Transactional(readOnly = true)
    public ConfiguracionBfa porVersion(Long idVersionFormulario) {
        VersionFormulario version =
                versionRepository
                        .buscarPorId(idVersionFormulario)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "Version de formulario no encontrada: "
                                                        + idVersionFormulario));
        Test test =
                testRepository
                        .buscarPorCodigo(ConfiguracionBfa.CODIGO_TEST)
                        .orElseThrow(() -> new NoSuchElementException("Test BFA no encontrado"));
        if (!test.getIdTest().equals(version.getIdTest())) {
            throw new IllegalArgumentException("La version no pertenece al test BFA");
        }
        return ConfiguracionBfa.construir(
                version.getIdVersionFormulario(),
                subpruebaRepository.listarPorTest(test.getIdTest()),
                configuracionRepository.listarPorVersion(version.getIdVersionFormulario()));
    }
}
