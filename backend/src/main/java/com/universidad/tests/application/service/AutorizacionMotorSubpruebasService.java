package com.universidad.tests.application.service;

import com.universidad.tests.domain.exception.SinPermisoException;
import com.universidad.tests.domain.model.Rol;
import com.universidad.tests.domain.port.AlcanceMotorSubpruebasPort;
import com.universidad.tests.domain.port.AutorizacionMotorSubpruebasPort;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AutorizacionMotorSubpruebasService implements AutorizacionMotorSubpruebasPort {
    private final AutorizacionService autorizacion;
    private final Optional<AlcanceMotorSubpruebasPort> alcance;

    public AutorizacionMotorSubpruebasService(
            AutorizacionService autorizacion, Optional<AlcanceMotorSubpruebasPort> alcance) {
        this.autorizacion = autorizacion;
        this.alcance = alcance;
    }

    @Override
    public void exigirPermiso(Permiso permiso) {
        var identidad = autorizacion.exigirPermiso(permiso.name());
        // CU-BFA-P06 reserva la escritura al sujeto evaluado.
        if (permiso == Permiso.RESPUESTA_ESCRIBIR
                && !Rol.SUJETO.name().equals(identidad.rolActivo())) {
            throw new SinPermisoException();
        }
        // El vínculo cuenta-sujeto y el alcance del aplicador requieren datos institucionales.
        if (alcance.isEmpty()) {
            throw new SinPermisoException();
        }
    }

    @Override
    public void exigirAcceso(Permiso permiso, Long evaluacionId, Long sujetoId) {
        var identidad = autorizacion.exigirSesion();
        boolean permitido =
                alcance.map(
                                a ->
                                        a.permite(
                                                identidad.idCuenta(),
                                                identidad.rolActivo(),
                                                permiso,
                                                evaluacionId,
                                                sujetoId))
                        .orElse(false);
        if (!permitido) {
            throw new SinPermisoException();
        }
    }
}
