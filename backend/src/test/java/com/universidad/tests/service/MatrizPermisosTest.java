package com.universidad.tests.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.universidad.tests.domain.model.MatrizPermisos;
import com.universidad.tests.domain.model.Rol;
import org.junit.jupiter.api.Test;

// GEN-33-BE: matriz aprobada (contrato + Documento general CU-GEN-04, CU-GEN-19 y CU-GEN-20)
class MatrizPermisosTest {

    private final MatrizPermisos matriz = MatrizPermisos.aprobada();

    @Test
    void sujetoConsultaSuIdentidadYRespondeEnSuAsignacion() {
        assertThat(matriz.permisosDe(Rol.SUJETO))
                .containsExactlyInAnyOrder(
                        "SUJETO_LEER",
                        "PERIODO_LEER",
                        "RECUPERAR_INTENTO",
                        "EVALUACION_FINALIZAR",
                        "SUBPRUEBA_INICIAR",
                        "ITEMS_LEER",
                        "RESPUESTA_ESCRIBIR");
    }

    @Test
    void academicoCreaYLeeSujetosYConduceAplicaciones() {
        assertThat(matriz.permisosDe(Rol.ACADEMICO))
                .containsExactlyInAnyOrder(
                        "SUJETO_CREAR",
                        "SUJETO_LEER",
                        "PERIODO_LEER",
                        "RECUPERAR_INTENTO",
                        "OPERACION_AUTORIZAR");
    }

    @Test
    void coordinadorConfiguraElTestE25aE31YPublicaResultados() {
        assertThat(matriz.permisosDe(Rol.COORDINADOR))
                .containsExactlyInAnyOrder(
                        "CONFIGURAR_BFA",
                        "INSTRUCCIONES_EDITAR",
                        "CONTENIDO_EDITAR",
                        "OPCIONES_EDITAR",
                        "PUBLICAR_RESULTADOS",
                        "SUJETO_CREAR",
                        "SUJETO_LEER",
                        "PERIODO_CREAR",
                        "PERIODO_LEER",
                        "RECUPERAR_INTENTO",
                        "OPERACION_AUTORIZAR");
    }

    @Test
    void psicologoRevisaFluidezRecalculaYPublica() {
        assertThat(matriz.permisosDe(Rol.PSICOLOGO))
                .containsExactlyInAnyOrder(
                        "REVISAR_FLUIDEZ",
                        "RECALCULAR_BFA",
                        "PUBLICAR_RESULTADOS",
                        "SUJETO_LEER",
                        "PERIODO_LEER");
    }

    @Test
    void decanoNoObtieneFacultadesDeCapturaNiAdministracion() {
        assertThat(matriz.permisosDe(Rol.DECANO))
                .containsExactlyInAnyOrder("SUJETO_LEER", "PERIODO_LEER");
    }

    @Test
    void soloPsicologoYCoordinadorPublicanYSoloPsicologoRevisaFluidez() {
        for (Rol rol : Rol.values()) {
            boolean publica = rol == Rol.PSICOLOGO || rol == Rol.COORDINADOR;
            assertThat(matriz.permisosDe(rol).contains("PUBLICAR_RESULTADOS")).isEqualTo(publica);
            assertThat(matriz.permisosDe(rol).contains("REVISAR_FLUIDEZ"))
                    .isEqualTo(rol == Rol.PSICOLOGO);
        }
    }

    @Test
    void lasCopiasDeLaMatrizNoSePuedenModificar() {
        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class,
                () -> matriz.permisosDe(Rol.SUJETO).add("CONFIGURAR_BFA"));
    }
}
