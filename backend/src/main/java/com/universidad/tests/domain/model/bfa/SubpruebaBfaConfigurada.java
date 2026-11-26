package com.universidad.tests.domain.model.bfa;

/** Una subprueba oficial del BFA ya asociada a su registro del catalogo y a su configuracion. */
public record SubpruebaBfaConfigurada(
        SubpruebaBfa subprueba, Long idSubprueba, Long idConfiguracionSubprueba) {}
