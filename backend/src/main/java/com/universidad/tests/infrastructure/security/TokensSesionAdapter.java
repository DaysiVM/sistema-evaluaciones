package com.universidad.tests.infrastructure.security;

import com.universidad.tests.domain.port.TokensSesionPort;
import org.springframework.stereotype.Component;

// Expone al dominio los tokens opacos de TokensSesionSeguros sin modificarlos
@Component
public class TokensSesionAdapter implements TokensSesionPort {

    private final TokensSesionSeguros tokens = new TokensSesionSeguros();

    @Override
    public String generar() {
        return tokens.generar();
    }

    @Override
    public String resumirSiValido(String token) {
        try {
            return tokens.resumir(token);
        } catch (IllegalArgumentException formatoInvalido) {
            return null;
        }
    }
}
