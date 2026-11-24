package com.universidad.tests.application.usecase;

import java.util.List;

/**
 * Datos de E3. {@code subpruebas} nulo significa aplicación completa; {@code letraInicial} solo
 * aplica si el test la exige (FV en BFA).
 */
public record IniciarEvaluacionComando(
        Long testId,
        Long versionFormularioId,
        Long sujetoId,
        Long periodoId,
        List<String> subpruebas,
        String letraInicial) {}
