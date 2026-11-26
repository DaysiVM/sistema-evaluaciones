package com.universidad.tests.application.usecase.bfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.universidad.tests.domain.model.VersionFormulario;
import com.universidad.tests.domain.model.bfa.ConfiguracionBfa;
import com.universidad.tests.domain.model.bfa.ConfiguracionBfaFixture;
import com.universidad.tests.domain.repository.ConfiguracionSubpruebaRepository;
import com.universidad.tests.domain.repository.SubpruebaRepository;
import com.universidad.tests.domain.repository.TestRepository;
import com.universidad.tests.domain.repository.VersionFormularioRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsultarConfiguracionBfaUseCaseTest {

    private static final long TEST_ID = 1L;
    private static final long VERSION_ID = 10L;

    private TestRepository tests;
    private VersionFormularioRepository versiones;
    private SubpruebaRepository subpruebas;
    private ConfiguracionSubpruebaRepository configuraciones;
    private ConsultarConfiguracionBfaUseCase useCase;

    @BeforeEach
    void setUp() {
        tests = mock(TestRepository.class);
        versiones = mock(VersionFormularioRepository.class);
        subpruebas = mock(SubpruebaRepository.class);
        configuraciones = mock(ConfiguracionSubpruebaRepository.class);
        useCase =
                new ConsultarConfiguracionBfaUseCase(tests, versiones, subpruebas, configuraciones);
    }

    private VersionFormulario version(long idTest) {
        return new VersionFormulario(VERSION_ID, idTest, "2026-1", null, "doc/bfa");
    }

    @Test
    void devuelveConfiguracionValidada() {
        when(versiones.buscarPorId(VERSION_ID)).thenReturn(Optional.of(version(TEST_ID)));
        when(tests.buscarPorCodigo("BFA"))
                .thenReturn(
                        Optional.of(
                                new com.universidad.tests.domain.model.Test(
                                        TEST_ID, "BFA", "Bateria BFA", null, true)));
        when(subpruebas.listarPorTest(TEST_ID))
                .thenReturn(ConfiguracionBfaFixture.catalogoOficial());
        when(configuraciones.listarPorVersion(VERSION_ID))
                .thenReturn(ConfiguracionBfaFixture.configuracionOficial());

        ConfiguracionBfa cfg = useCase.porVersion(VERSION_ID);

        assertEquals(13, cfg.getSubpruebas().size());
    }

    @Test
    void versionInexistenteLanzaNoSuchElement() {
        when(versiones.buscarPorId(VERSION_ID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> useCase.porVersion(VERSION_ID));
    }

    @Test
    void versionDeOtroTestSeRechaza() {
        when(versiones.buscarPorId(VERSION_ID)).thenReturn(Optional.of(version(99L)));
        when(tests.buscarPorCodigo("BFA"))
                .thenReturn(
                        Optional.of(
                                new com.universidad.tests.domain.model.Test(
                                        TEST_ID, "BFA", "Bateria BFA", null, true)));
        assertThrows(IllegalArgumentException.class, () -> useCase.porVersion(VERSION_ID));
    }
}
