package com.universidad.tests.infrastructure.security;

import com.universidad.tests.domain.port.CorreoRecuperacionPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Sin SMTP configurado falla de forma explicita; no imprime ni simula envio de tokens. */
@Component
public class CorreoRecuperacionSmtpAdapter implements CorreoRecuperacionPort {
    private final ObjectProvider<JavaMailSender> proveedor;
    private final String remitente;

    public CorreoRecuperacionSmtpAdapter(
            ObjectProvider<JavaMailSender> proveedor,
            @Value("${seguridad.recuperacion.remitente:}") String remitente) {
        this.proveedor = proveedor;
        this.remitente = remitente;
    }

    public boolean disponible() {
        return proveedor.getIfAvailable() != null && !remitente.isBlank();
    }

    public void enviar(String destino, String token) {
        JavaMailSender sender = proveedor.getIfAvailable();
        if (sender == null || remitente.isBlank())
            throw new IllegalStateException("Correo no configurado");
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destino);
        mensaje.setSubject("Recuperacion de acceso");
        mensaje.setText("Token de un solo uso, vigente por 15 minutos: " + token);
        sender.send(mensaje);
    }
}
