package com.universidad.tests.resultados;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.universidad.tests.domain.model.resultados.FactorResultado;
import org.junit.jupiter.api.Test;

/** GEN-05: qué valores de un factor se pueden presentar como definitivos. */
class FactorResultadoTest {

    @Test
    void factorCalculadoYConvertidoConservaTodosSusValores() {
        FactorResultado factor =
                new FactorResultado("VOC1", 23, "CALCULADO", 60, 7L, "CONVERTIDO", null);

        assertThat(factor.paraPresentar()).isEqualTo(factor);
    }

    @Test
    void ceroCalculadoSeConservaComoCero() {
        FactorResultado factor =
                new FactorResultado(
                        "CV", 0, "CALCULADO", null, null, "SIN_BAREMO_VALIDADO", "Sin baremo");

        FactorResultado presentado = factor.paraPresentar();

        assertThat(presentado.puntuacionDirecta()).isZero();
        assertThat(presentado.percentil()).isNull();
        assertThat(presentado.motivo()).isEqualTo("Sin baremo");
    }

    @Test
    void factorNoCalculableNoPresentaPuntuacionNiPercentil() {
        FactorResultado factor =
                new FactorResultado(
                        "FV", 12, "NO_CALCULABLE", 40, 3L, "CONVERTIDO", "Aplicación parcial");

        FactorResultado presentado = factor.paraPresentar();

        assertThat(presentado.puntuacionDirecta()).isNull();
        assertThat(presentado.percentil()).isNull();
        assertThat(presentado.idRango()).isNull();
        assertThat(presentado.estadoCalculo()).isEqualTo("NO_CALCULABLE");
        assertThat(presentado.motivo()).isEqualTo("Aplicación parcial");
    }

    @Test
    void factorPendienteDeValidacionNoSePresentaComoDefinitivo() {
        FactorResultado factor =
                new FactorResultado(
                        "FV", 18, "PENDIENTE_VALIDACION", null, null, "NO_APLICA", "Palabras");

        assertThat(factor.paraPresentar().puntuacionDirecta()).isNull();
    }

    @Test
    void percentilSinCoincidenciaEnElBaremoSeEntregaNulo() {
        FactorResultado factor =
                new FactorResultado(
                        "R1", 31, "CALCULADO", 55, 9L, "SIN_COINCIDENCIA", "Fuera de rango");

        FactorResultado presentado = factor.paraPresentar();

        assertThat(presentado.puntuacionDirecta()).isEqualTo(31);
        assertThat(presentado.percentil()).isNull();
        assertThat(presentado.idRango()).isNull();
    }

    @Test
    void percentilConvertidoSinRangoExactoSeEntregaNulo() {
        FactorResultado factor =
                new FactorResultado("S1", 14, "CALCULADO", 50, null, "CONVERTIDO", null);

        assertThat(factor.paraPresentar().percentil()).isNull();
    }

    @Test
    void estadoDesconocidoNuncaHabilitaValores() {
        FactorResultado factor = new FactorResultado("X", 5, "OTRO", 10, 1L, "OTRO", null);

        FactorResultado presentado = factor.paraPresentar();

        assertThat(presentado.puntuacionDirecta()).isNull();
        assertThat(presentado.percentil()).isNull();
        assertThat(presentado.estadoCalculo()).isEqualTo("OTRO");
    }

    @Test
    void exigeCodigoYEstados() {
        assertThatThrownBy(() -> new FactorResultado(null, 1, "CALCULADO", null, null, "X", null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new FactorResultado("A", 1, null, null, null, "X", null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new FactorResultado("A", 1, "CALCULADO", null, null, null, null))
                .isInstanceOf(NullPointerException.class);
    }
}
