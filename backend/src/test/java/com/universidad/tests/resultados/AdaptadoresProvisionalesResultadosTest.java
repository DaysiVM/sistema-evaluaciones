package com.universidad.tests.resultados;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException;
import com.universidad.tests.domain.exception.resultados.ConsultaResultadoException.Motivo;
import com.universidad.tests.infrastructure.persistence.resultados.AuditoriaResultadoRegistroAdapter;
import com.universidad.tests.infrastructure.persistence.resultados.ConsultaResultadosPendienteAdapter;
import com.universidad.tests.infrastructure.security.resultados.AutorizacionResultadoDenegadaAdapter;
import org.junit.jupiter.api.Test;

/** GEN-05: los adaptadores provisionales no exponen ni inventan resultados. */
class AdaptadoresProvisionalesResultadosTest {

    @Test
    void autorizacionProvisionalNiegaTodaConsulta() {
        AutorizacionResultadoDenegadaAdapter autorizacion =
                new AutorizacionResultadoDenegadaAdapter();

        assertThatThrownBy(autorizacion::exigirPermisoConsultarResultados)
                .isInstanceOf(ConsultaResultadoException.class)
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.SIN_PERMISO);
        assertThatThrownBy(
                        () ->
                                autorizacion.exigirAlcanceSobre(
                                        ResultadosDatosPrueba.finalizada(16L)))
                .extracting(e -> ((ConsultaResultadoException) e).motivo())
                .isEqualTo(Motivo.SIN_PERMISO);
    }

    @Test
    void lecturaProvisionalFallaEnLugarDeInventarDatos() {
        ConsultaResultadosPendienteAdapter lectura = new ConsultaResultadosPendienteAdapter();

        assertThatThrownBy(() -> lectura.buscarPorId(16L))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> lectura.buscarPorEvaluacion(16L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void auditoriaProvisionalRegistraSinFallar() {
        assertThatCode(
                        () ->
                                new AuditoriaResultadoRegistroAdapter()
                                        .registrarConsulta(16L, 101L, null))
                .doesNotThrowAnyException();
    }
}
