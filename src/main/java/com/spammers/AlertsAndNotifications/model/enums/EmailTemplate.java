package com.spammers.AlertsAndNotifications.model.enums;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    /**
     * This Notification Alert Template Gets 1 argument and refers to the notification type might be loan return, or loan created.
     */
    NOTIFICATION_ALERT(
            "Nueva Notificación - BibloSoft.",
            """
            Hola!,
                   \s
            Tienes una nueva notificación:
            %s
                   \s
            Para más detalles, ingresa a tu cuenta.
                   \s
            Saludos,
            El equipo de BibloSoft.
           \s
            Este es un mensaje automático. No responder a este mensaje.
           \s"""
    ),

    /**
     * This Fine alert template receives 4 args, according to the creation/closure amount, date and description of the fine.
     */
    FINE_ALERT(
            "Alerta de Multa - BibloSoft.",
            """
            Hola!,
                   \s
            %s
            Monto: %s
            Fecha: %s
            Descripción: %s
                   \s
            Por favor, revisa los detalles en tu cuenta.
                   \s
            Atentamente,
            El equipo de BibloSoft.
           \s
            Este es un mensaje automático. No responder a este mensaje.
           \s"""
    );

    private final String subject;
    private final String template;

    EmailTemplate(String subject, String template) {
        this.subject = subject;
        this.template = template;
    }

    public String formatBody(Object... args) {
        return String.format(template, args);
    }
}
