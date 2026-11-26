package com.universidad.tests.domain.repository;

import com.universidad.tests.domain.model.Cuenta;
import java.util.Optional;

public interface CuentaRepository {

    Optional<Cuenta> buscarPorId(Long idCuenta);

    // El correo se compara sin distinguir mayusculas
    Optional<Cuenta> buscarPorCorreo(String correo);

    boolean existePorCorreo(String correo);

    Cuenta guardar(Cuenta cuenta);
}
