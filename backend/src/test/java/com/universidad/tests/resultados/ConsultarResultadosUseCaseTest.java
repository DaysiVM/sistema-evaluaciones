package com.universidad.tests.resultados;

import static com.universidad.tests.resultados.ResultadosDatosPrueba.EV_CALCULANDO;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.EV_DISPONIBLE;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.EV_EN_CURSO;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.EV_PENDIENTE;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.EV_SIN_RESULTADO;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.VERSION_FORMULARIO;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.disponible;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.enCurso;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.finalizada;
import static com.universidad.tests.resultados.ResultadosDatosPrueba.sinRevision;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.universidad.tests.application.usecase.resultados.ConsultarResultadosUseCase;
import com.universidad.tests.application.usecase.resultados.ResultadoVista;
import com.universidad.tests.application.usecase.resultados.ResultadoVista.FactorVista;
import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException;
import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException.Motivo;
import com.universidad.tests.domain.model.resultados.ContenedorResultado;
import com.universidad.tests.domain.port.resultados.AuditoriaResultadoPort;
import com.universidad.tests.domain.port.resultados.AutorizacionResultadoPort;
import com.universidad.tests.domain.port.resultados.EvaluacionConsultaPort;
import com.universidad.tests.domain.port.resultados.ResultadoConsultaPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultarResultadosUseCaseTest {

    @Mock private AutorizacionResultadoPort autorizacion;
    @Mock private EvaluacionConsultaPort evaluaciones;
    @Mock private ResultadoConsultaPort resultados;
    @Mock private AuditoriaResultadoPort auditoria;

    private ConsultarResultadosUseCase casoDeUso;

    @BeforeEach
    void crear() {
        casoDeUso =
                new ConsultarResultadosUseCase(autorizacion, evaluaciones, resultados, auditoria);
    }

    // ----- Resultados disponibles, pendientes y en cálculo -----

    @Test
    void disponibleDevuelveLaRevisionVigenteConSusVersionesYFactoresEnOrden() {
        when(evaluaciones.buscarPorId(EV_DISPONIBLE))
                .thenReturn(Optional.of(finalizada(EV_DISPONIBLE)));
        when(resultados.buscarPorEvaluacion(EV_DISPONIBLE))
                .thenReturn(Optional.of(disponible(EV_DISPONIBLE)));

        ResultadoVista vista = casoDeUso.consultar(EV_DISPONIBLE);

        assertThat(vista.evaluacionId()).isEqualTo(EV_DISPONIBLE);
        assertThat(vista.resultadoId()).isEqualTo(101L);
        assertThat(vista.estadoResultado()).isEqualTo("DISPONIBLE");
        assertThat(vista.versionFormularioId()).isEqualTo(VERSION_FORMULARIO);
        assertThat(vista.calificacionId()).isEqualTo(501L);
        assertThat(vista.revision()).isEqualTo(1);
        assertThat(vista.versionReglas()).isEqualTo("BFA-REGLAS-v1");
        assertThat(vista.idClave()).isEqualTo(11L);
        assertThat(vista.idBaremo()).isEqualTo(21L);
        assertThat(vista.factores())
                .extracting(FactorVista::codigo)
                .containsExactly("VOC1", "R1", "CV", "FV");
    }

    @Test
    void disponibleNoPresentaPuntuacionesNiPercentilesInexistentes() {
        when(evaluaciones.buscarPorId(EV_DISPONIBLE))
                .thenReturn(Optional.of(finalizada(EV_DISPONIBLE)));
        when(resultados.buscarPorEvaluacion(EV_DISPONIBLE))
                .thenReturn(Optional.of(disponible(EV_DISPONIBLE)));

        ResultadoVista vista = casoDeUso.consultar(EV_DISPONIBLE);

        FactorVista voc1 = vista.factores().get(0);
        FactorVista r1 = vista.factores().get(1);
        FactorVista cv = vista.factores().get(2);
        FactorVista fv = vista.factores().get(3);
        assertThat(voc1.puntuacionDirecta()).isEqualTo(23);
        assertThat(voc1.percentil()).isEqualTo(60);
        assertThat(r1.percentil()).as("sin coincidencia en el baremo").isNull();
        assertThat(r1.motivo()).isEqualTo("Fuera de rango");
        assertThat(cv.puntuacionDirecta()).as("un cero calculado sigue siendo cero").isZero();
        assertThat(fv.puntuacionDirecta()).as("no calculable").isNull();
        assertThat(fv.percentil()).isNull();
        assertThat(fv.motivo()).isEqualTo("Aplicación parcial");
    }

    @Test
    void pendienteDeValidacionSinRevisionNoDevuelveFactoresNiIdentificadores() {
        when(evaluaciones.buscarPorId(EV_PENDIENTE))
                .thenReturn(Optional.of(finalizada(EV_PENDIENTE)));
        when(resultados.buscarPorEvaluacion(EV_PENDIENTE))
                .thenReturn(Optional.of(sinRevision(EV_PENDIENTE, "PENDIENTE_VALIDACION")));

        ResultadoVista vista = casoDeUso.consultar(EV_PENDIENTE);

        assertThat(vista.estadoResultado()).isEqualTo("PENDIENTE_VALIDACION");
        assertThat(vista.resultadoId()).isEqualTo(102L);
        assertThat(vista.calificacionId()).isNull();
        assertThat(vista.revision()).isNull();
        assertThat(vista.versionReglas()).isNull();
        assertThat(vista.idClave()).isNull();
        assertThat(vista.idBaremo()).isNull();
        assertThat(vista.factores()).isEmpty();
    }

    @Test
    void calculandoSinRevisionNoDevuelveFactores() {
        when(evaluaciones.buscarPorId(EV_CALCULANDO))
                .thenReturn(Optional.of(finalizada(EV_CALCULANDO)));
        when(resultados.buscarPorEvaluacion(EV_CALCULANDO))
                .thenReturn(Optional.of(sinRevision(EV_CALCULANDO, "CALCULANDO")));

        ResultadoVista vista = casoDeUso.consultar(EV_CALCULANDO);

        assertThat(vista.estadoResultado()).isEqualTo("CALCULANDO");
        assertThat(vista.calificacionId()).isNull();
        assertThat(vista.factores()).isEmpty();
    }

    @Test
    void finalizadaSinResultadoGuardadoSeInformaComoCalculando() {
        when(evaluaciones.buscarPorId(EV_SIN_RESULTADO))
                .thenReturn(Optional.of(finalizada(EV_SIN_RESULTADO)));
        when(resultados.buscarPorEvaluacion(EV_SIN_RESULTADO)).thenReturn(Optional.empty());

        ResultadoVista vista = casoDeUso.consultar(EV_SIN_RESULTADO);

        assertThat(vista.estadoResultado()).isEqualTo("CALCULANDO");
        assertThat(vista.resultadoId()).isNull();
        assertThat(vista.versionFormularioId()).isEqualTo(VERSION_FORMULARIO);
        assertThat(vista.factores()).isEmpty();
    }

    @Test
    void disponibleSinRevisionVigenteSeRechazaComoInconsistente() {
        when(evaluaciones.buscarPorId(EV_DISPONIBLE))
                .thenReturn(Optional.of(finalizada(EV_DISPONIBLE)));
        when(resultados.buscarPorEvaluacion(EV_DISPONIBLE))
                .thenReturn(Optional.of(sinRevision(EV_DISPONIBLE, "DISPONIBLE")));

        assertThatThrownBy(() -> casoDeUso.consultar(EV_DISPONIBLE))
                .isInstanceOf(ConsultaResultadoException.class)
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.RESULTADO_INCONSISTENTE);
        verifyNoInteractions(auditoria);
    }

    @Test
    void resultadoDeOtraEvaluacionSeRechazaComoInconsistente() {
        when(evaluaciones.buscarPorId(EV_DISPONIBLE))
                .thenReturn(Optional.of(finalizada(EV_DISPONIBLE)));
        when(resultados.buscarPorEvaluacion(EV_DISPONIBLE))
                .thenReturn(Optional.of(disponible(99L)));

        assertThatThrownBy(() -> casoDeUso.consultar(EV_DISPONIBLE))
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.RESULTADO_INCONSISTENTE);
    }

    // ----- Errores de consulta -----

    @Test
    void idNoPositivoEsSolicitudInvalidaSinConsultarNada() {
        assertThatThrownBy(() -> casoDeUso.consultar(0L))
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.SOLICITUD_INVALIDA);
        assertThatThrownBy(() -> casoDeUso.consultar(null))
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.SOLICITUD_INVALIDA);
        verifyNoInteractions(autorizacion, evaluaciones, resultados, auditoria);
    }

    @Test
    void evaluacionInexistenteEsNoEncontrada() {
        when(evaluaciones.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> casoDeUso.consultar(999L))
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.EVALUACION_NO_ENCONTRADA);
        verifyNoInteractions(resultados, auditoria);
    }

    @Test
    void evaluacionEnCursoEsNoFinalizadaYNoLeeResultados() {
        when(evaluaciones.buscarPorId(EV_EN_CURSO)).thenReturn(Optional.of(enCurso(EV_EN_CURSO)));

        assertThatThrownBy(() -> casoDeUso.consultar(EV_EN_CURSO))
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.EVALUACION_NO_FINALIZADA);
        verifyNoInteractions(resultados, auditoria);
    }

    // ----- Permisos -----

    @Test
    void sinPermisoNoBuscaLaEvaluacionNiRevelaSiExiste() {
        doThrow(ConsultaResultadoException.sinPermiso())
                .when(autorizacion)
                .exigirPermisoConsultarResultados();

        assertThatThrownBy(() -> casoDeUso.consultar(EV_DISPONIBLE))
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.SIN_PERMISO);
        verifyNoInteractions(evaluaciones, resultados, auditoria);
    }

    @Test
    void fueraDeAlcanceNoLeeElResultado() {
        when(evaluaciones.buscarPorId(EV_DISPONIBLE))
                .thenReturn(Optional.of(finalizada(EV_DISPONIBLE)));
        doThrow(ConsultaResultadoException.sinPermiso())
                .when(autorizacion)
                .exigirAlcanceSobre(any());

        assertThatThrownBy(() -> casoDeUso.consultar(EV_DISPONIBLE))
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.SIN_PERMISO);
        verifyNoInteractions(resultados, auditoria);
    }

    @Test
    void fueraDeAlcanceSeRechazaAntesDeRevelarSiFinalizo() {
        when(evaluaciones.buscarPorId(EV_EN_CURSO)).thenReturn(Optional.of(enCurso(EV_EN_CURSO)));
        doThrow(ConsultaResultadoException.sinPermiso())
                .when(autorizacion)
                .exigirAlcanceSobre(any());

        assertThatThrownBy(() -> casoDeUso.consultar(EV_EN_CURSO))
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.SIN_PERMISO);
    }

    // ----- Auditoría -----

    @Test
    void registraLaConsultaConLosIdentificadoresDevueltos() {
        when(evaluaciones.buscarPorId(EV_DISPONIBLE))
                .thenReturn(Optional.of(finalizada(EV_DISPONIBLE)));
        when(resultados.buscarPorEvaluacion(EV_DISPONIBLE))
                .thenReturn(Optional.of(disponible(EV_DISPONIBLE)));

        casoDeUso.consultar(EV_DISPONIBLE);

        InOrder orden = inOrder(autorizacion, evaluaciones, resultados, auditoria);
        orden.verify(autorizacion).exigirPermisoConsultarResultados();
        orden.verify(evaluaciones).buscarPorId(EV_DISPONIBLE);
        orden.verify(autorizacion).exigirAlcanceSobre(finalizada(EV_DISPONIBLE));
        orden.verify(resultados).buscarPorEvaluacion(EV_DISPONIBLE);
        orden.verify(auditoria).registrarConsulta(EV_DISPONIBLE, 101L, 501L);
    }

    @Test
    void registraLaConsultaDeUnResultadoPendienteSinCalificacion() {
        when(evaluaciones.buscarPorId(EV_PENDIENTE))
                .thenReturn(Optional.of(finalizada(EV_PENDIENTE)));
        when(resultados.buscarPorEvaluacion(EV_PENDIENTE))
                .thenReturn(
                        Optional.of(
                                sinRevision(
                                        EV_PENDIENTE, ContenedorResultado.PENDIENTE_VALIDACION)));

        casoDeUso.consultar(EV_PENDIENTE);

        verify(auditoria).registrarConsulta(EV_PENDIENTE, 102L, null);
    }
}
