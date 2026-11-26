package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.model.Cuenta;
import com.universidad.tests.domain.model.EstadoCuenta;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.repository.CuentaRepository;
import com.universidad.tests.infrastructure.persistence.entity.CuentaJpaEntity;
import com.universidad.tests.infrastructure.persistence.repository.CuentaJpaRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CuentaPersistenceAdapter implements CuentaRepository {

    private final CuentaJpaRepository jpaRepository;

    public CuentaPersistenceAdapter(CuentaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cuenta> buscarPorId(Long idCuenta) {
        return jpaRepository.findById(idCuenta).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cuenta> buscarPorCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return Optional.empty();
        }
        return jpaRepository.buscarPorCorreo(correo).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCorreo(String correo) {
        return correo != null && !correo.isBlank() && jpaRepository.existePorCorreo(correo);
    }

    @Override
    @Transactional
    public Cuenta guardar(Cuenta cuenta) {

        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta es obligatoria");
        }

        Set<String> roles = cuenta.getRoles().stream().map(Rol::name).collect(Collectors.toSet());
        CuentaJpaEntity entidad;

        if (cuenta.getIdCuenta() == null) {
            entidad =
                    new CuentaJpaEntity(
                            cuenta.getCorreo(),
                            cuenta.getResumenContrasena(),
                            cuenta.getNombres(),
                            cuenta.getApellidos(),
                            cuenta.getEstado().name(),
                            roles);
        } else {
            entidad =
                    jpaRepository
                            .findById(cuenta.getIdCuenta())
                            .orElseThrow(
                                    () ->
                                            new NoSuchElementException(
                                                    "No existe la cuenta con ID: "
                                                            + cuenta.getIdCuenta()));
            entidad.actualizarDatos(
                    cuenta.getResumenContrasena(),
                    cuenta.getNombres(),
                    cuenta.getApellidos(),
                    cuenta.getEstado().name(),
                    roles);
        }

        return aDominio(jpaRepository.saveAndFlush(entidad));
    }

    private Cuenta aDominio(CuentaJpaEntity entidad) {
        return new Cuenta(
                entidad.getIdCuenta(),
                entidad.getCorreo(),
                entidad.getResumenContrasena(),
                entidad.getNombres(),
                entidad.getApellidos(),
                EstadoCuenta.valueOf(entidad.getEstado()),
                entidad.getRoles().stream().map(Rol::valueOf).collect(Collectors.toSet()));
    }
}
