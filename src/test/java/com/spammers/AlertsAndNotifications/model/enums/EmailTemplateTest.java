package com.spammers.AlertsAndNotifications.model.enums;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
class EmailTemplateTest {
    private EmailTemplate emailTemplate;

    @Test
    void getSubject() {
        assertEquals("Nueva Notificación - BibloSoft.",emailTemplate.NOTIFICATION_ALERT.getSubject());
        assertEquals("Alerta de Multa - BibloSoft.", emailTemplate.FINE_ALERT.getSubject());
    }

    @Test
    void getTemplateNotificationAlert() {
        String expectedTemplate = """
        Hola!,
                
        Tienes una nueva notificación:
        %s
                
        Para más detalles, ingresa a tu cuenta.
                
        Saludos,
        El equipo de BibloSoft.
        
        Este es un mensaje automático. No responder a este mensaje.
        """;
        assertEquals(expectedTemplate, EmailTemplate.NOTIFICATION_ALERT.getTemplate());
    }

    @Test
    void getTemplateFineAlert() {
        String expectedTemplate = """
        Hola!,
                
        Se ha registrado una nueva multa:
        Monto: %s
        Fecha: %s
        Descripción: %s
                
        Por favor, revisa los detalles en tu cuenta.
                
        Atentamente,
        El equipo de BibloSoft.
        
        Este es un mensaje automático. No responder a este mensaje.
        """;
        assertEquals(expectedTemplate, EmailTemplate.FINE_ALERT.getTemplate());
    }

    @Test
    void getFormatBodyNotificationAlert() {
        String message = "Tu préstamo ha sido creado exitosamente.";
        String expectedMessage = """
        Hola!,
                
        Tienes una nueva notificación:
        Tu préstamo ha sido creado exitosamente.
                
        Para más detalles, ingresa a tu cuenta.
                
        Saludos,
        El equipo de BibloSoft.
        
        Este es un mensaje automático. No responder a este mensaje.
        """;
        String formattedMessage = EmailTemplate.NOTIFICATION_ALERT.formatBody(message);
        assertEquals(expectedMessage, formattedMessage);
    }

    @Test
    void getFormatBodyFineAlert() {
        String amount = "$50";
        String date = "2024-11-21";
        String description = "Multa por devolución tardía.";
        String expectedMessage = """
        Hola!,
                
        Se ha registrado una nueva multa:
        Monto: $50
        Fecha: 2024-11-21
        Descripción: Multa por devolución tardía.
                
        Por favor, revisa los detalles en tu cuenta.
                
        Atentamente,
        El equipo de BibloSoft.
        
        Este es un mensaje automático. No responder a este mensaje.
        """;
        String formattedMessage = EmailTemplate.FINE_ALERT.formatBody(amount, date, description);
        assertEquals(expectedMessage, formattedMessage);
    }
}