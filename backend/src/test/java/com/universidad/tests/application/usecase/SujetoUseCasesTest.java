package com.universidad.tests.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.universidad.tests.application.service.AutorizacionService;
import com.universidad.tests.application.service.IdentidadSesion;
import com.universidad.tests.application.usecase.vista.SujetoVista;
import com.universidad.tests.domain.exception.ErrorNegocioException;
import com.universidad.tests.domain.model.Sujeto;
import com.universidad.tests.domain.port.AlcanceSujetoPort;
import com.universidad.tests.domain.port.AutorizacionSujetoPort;
import com.universidad.tests.domain.repository.SujetoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SujetoUseCasesTest {

    @Mock private AutorizacionSujetoPort autorizacion;
    @Mock private SujetoRepository sujetoRepository;
    @Mock private AlcanceSujetoPort alcance;
    @Mock private AutorizacionService sesiones;

    @InjectMocks private RegistrarSujetoUseCase registrarSujetoUseCase;
    @InjectMocks private BuscarSujetoUseCase buscarSujetoUseCase;

    @Test
    void deberiaRegistrarSujetoFicticioCorrectamente() {
        when(sujetoRepository.buscarPorCodigoInstitucional("2026-9901"))
                .thenReturn(Optional.empty());
        when(sujetoRepository.guardar(any(Sujeto.class)))
                .thenReturn(
                        new Sujeto(
                                101L,
                                "2026-9901",
                                "Carlos Ficticio",
                                "Mendoza Prueba",
                                LocalDate.of(2005, 4, 12)));

        SujetoVista resultado =
                registrarSujetoUseCase.registrar(
                        "2026-9901",
                        "Carlos Ficticio",
                        "Mendoza Prueba",
                        LocalDate.of(2005, 4, 12));

        assertThat(resultado.id()).isEqualTo(101L);
        assertThat(resultado.identificadorInstitucional()).isEqualTo("2026-9901");
        verify(autorizacion).exigirPermisoCrearSujeto();
    }

    @Test
    void deberiaLanzarSujetoDuplicadoCuandoCodigoYaExiste() {
        Sujeto existente = new Sujeto(50L, "2026-9901", "Ana Ficticia", "Lopez Prueba", null);
        when(sujetoRepository.buscarPorCodigoInstitucional("2026-9901"))
                .thenReturn(Optional.of(existente));

        assertThatThrownBy(
                        () ->
                                registrarSujetoUseCase.registrar(
                                        "2026-9901", "Otro Nombre", "Otro Apellido", null))
                .isInstanceOf(ErrorNegocioException.class)
                .hasFieldOrPropertyWithValue("codigo", "SUJETO_DUPLICADO");
    }

    @Test
    void deberiaBuscarSujetoPorIdentificadorYDevolverListaDeUnoOVacio() {
        when(sesiones.exigirSesion())
                .thenReturn(new IdentidadSesion(1L, 1L, "SUJETO", java.util.Set.of("SUJETO_LEER")));
        when(alcance.estaAsignado(1L, 50L)).thenReturn(true);
        Sujeto existente = new Sujeto(50L, "2026-9902", "Lucia Ficticia", "Gomez Prueba", null);
        when(sujetoRepository.buscarPorCodigoInstitucional("2026-9902"))
                .thenReturn(Optional.of(existente));
        when(sujetoRepository.buscarPorCodigoInstitucional("2026-0000"))
                .thenReturn(Optional.empty());

        List<SujetoVista> encontrado = buscarSujetoUseCase.buscarPorIdentificador("2026-9902");
        List<SujetoVista> noEncontrado = buscarSujetoUseCase.buscarPorIdentificador("2026-0000");

        assertThat(encontrado).hasSize(1);
        assertThat(encontrado.get(0).id()).isEqualTo(50L);
        assertThat(noEncontrado).isEmpty();
    }
}
