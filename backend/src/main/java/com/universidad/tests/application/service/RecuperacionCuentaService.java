package com.universidad.tests.application.service;

import com.universidad.tests.domain.exception.RecuperacionException;
import com.universidad.tests.domain.exception.RecuperacionException.Codigo;
import com.universidad.tests.domain.port.*;
import com.universidad.tests.domain.repository.CuentaRepository;
import java.time.Instant;
import java.util.Arrays;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecuperacionCuentaService {
    private final CuentaRepository cuentas;
    private final RecuperacionCuentaPort recuperaciones;
    private final CorreoRecuperacionPort correo;
    private final TokensSesionPort tokens;
    private final HashContrasenasPort contrasenas;

    public RecuperacionCuentaService(
            CuentaRepository cuentas,
            RecuperacionCuentaPort recuperaciones,
            CorreoRecuperacionPort correo,
            TokensSesionPort tokens,
            HashContrasenasPort contrasenas) {
        this.cuentas = cuentas;
        this.recuperaciones = recuperaciones;
        this.correo = correo;
        this.tokens = tokens;
        this.contrasenas = contrasenas;
    }

    @Transactional
    public void pedir(String destinatario) {
        // Mismo 503 para cualquier correo si el canal no esta configurado.
        if (!correo.disponible()) throw new RecuperacionException(Codigo.CORREO_NO_DISPONIBLE);
        var cuenta = cuentas.buscarPorCorreo(destinatario).filter(c -> c.estaActiva());
        if (cuenta.isEmpty()) return;
        String token = tokens.generar();
        Instant ahora = Instant.now();
        recuperaciones.reemplazar(
                cuenta.get().getIdCuenta(),
                tokens.resumirSiValido(token),
                ahora,
                ahora.plusSeconds(900));
        try {
            correo.enviar(cuenta.get().getCorreo(), token);
        } catch (RuntimeException error) {
            throw new RecuperacionException(Codigo.CORREO_NO_DISPONIBLE);
        }
    }

    @Transactional
    public void confirmar(String token, String nuevaContrasena) {
        if (nuevaContrasena == null
                || nuevaContrasena.isBlank()
                || nuevaContrasena.codePointCount(0, nuevaContrasena.length()) < 12)
            throw new RecuperacionException(Codigo.SOLICITUD_INVALIDA);
        String resumen = tokens.resumirSiValido(token);
        if (resumen == null) throw new RecuperacionException(Codigo.TOKEN_INVALIDO);
        char[] clave = nuevaContrasena.toCharArray();
        try {
            String hash = contrasenas.crear(clave);
            if (!recuperaciones.confirmar(resumen, hash, Instant.now()))
                throw new RecuperacionException(Codigo.TOKEN_INVALIDO);
        } catch (IllegalArgumentException invalida) {
            throw new RecuperacionException(Codigo.SOLICITUD_INVALIDA);
        } finally {
            Arrays.fill(clave, '\0');
        }
    }
}
