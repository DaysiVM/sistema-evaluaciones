package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Sesion;
import java.time.Instant;
import java.util.Optional;

public interface SesionRepository {

    Sesion guardar(Sesion sesion);

    Optional<Sesion> buscarPorResumenToken(String resumenToken);

    // Marca la sesion como cerrada; devuelve false si no existia o ya estaba cerrada
    boolean cerrar(String resumenToken, Instant cerradaEn);
}
