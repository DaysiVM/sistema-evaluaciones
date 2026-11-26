package com.universidad.tests.application.usecase;

import com.universidad.tests.domain.model.Cuenta;
import com.universidad.tests.domain.model.EstadoCuenta;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.port.HashContrasenasPort;
import com.universidad.tests.domain.repository.CuentaRepository;
import com.universidad.tests.dto.request.RegistroRequest;
import com.universidad.tests.dto.response.CuentaResponse;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarCuentaService {

    private final CuentaRepository cuentas;
    private final HashContrasenasPort contrasenas;

    public RegistrarCuentaService(CuentaRepository cuentas, HashContrasenasPort contrasenas) {
        this.cuentas = cuentas;
        this.contrasenas = contrasenas;
    }

    @Transactional
    public CuentaResponse registrar(RegistroRequest solicitud) {
        if (solicitud.nombres().isBlank() || solicitud.apellidos().isBlank()) {
            throw new RegistroCuentaException(
                    RegistroCuentaException.Codigo.SOLICITUD_INVALIDA,
                    "Los nombres y apellidos son obligatorios");
        }
        // El alta propia solo concede SUJETO. Los demas roles requieren una regla de
        // asignacion aprobada; no se deduce de quien tenga una sesion abierta.
        if (!Rol.SUJETO.name().equals(solicitud.rol())) {
            throw new RegistroCuentaException(
                    RegistroCuentaException.Codigo.ROL_NO_AUTORIZADO,
                    "No tiene permiso para asignar el rol solicitado");
        }
        if (cuentas.existePorCorreo(solicitud.correo())) {
            throw cuentaExistente();
        }
        char[] contrasena = solicitud.contrasena().toCharArray();
        String resumen;
        try {
            resumen = contrasenas.crear(contrasena);
        } catch (IllegalArgumentException codificacionInvalida) {
            throw new RegistroCuentaException(
                    RegistroCuentaException.Codigo.SOLICITUD_INVALIDA,
                    "La contrasena no es valida");
        } finally {
            Arrays.fill(contrasena, '\0');
        }
        Cuenta cuenta;
        try {
            cuenta =
                    cuentas.guardar(
                            new Cuenta(
                                    null,
                                    solicitud.correo(),
                                    resumen,
                                    solicitud.nombres(),
                                    solicitud.apellidos(),
                                    EstadoCuenta.ACTIVA,
                                    Set.of(Rol.SUJETO)));
        } catch (DataIntegrityViolationException conflicto) {
            // La comprobacion previa no excluye dos altas concurrentes. Solo la
            // restriccion de correo de la dependencia se traduce como duplicado.
            Throwable causa = conflicto.getMostSpecificCause();
            if (causa instanceof SQLException sql
                    && "23505".equals(sql.getSQLState())
                    && sql.getMessage() != null
                    && sql.getMessage().contains("uq_cuenta_correo")) {
                throw cuentaExistente();
            }
            throw conflicto;
        }
        return new CuentaResponse(
                cuenta.getIdCuenta(),
                cuenta.getCorreo(),
                Rol.SUJETO.name(),
                cuenta.getEstado().name());
    }

    private RegistroCuentaException cuentaExistente() {
        return new RegistroCuentaException(
                RegistroCuentaException.Codigo.CUENTA_EXISTENTE, "El correo ya tiene cuenta");
    }
}
