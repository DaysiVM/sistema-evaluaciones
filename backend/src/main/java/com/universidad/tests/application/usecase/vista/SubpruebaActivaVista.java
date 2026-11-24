package com.universidad.tests.application.usecase.vista;

import java.time.Instant;

public record SubpruebaActivaVista(String codigo, Instant finLimite, Long segundosRestantes) {}
