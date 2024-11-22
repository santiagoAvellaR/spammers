package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;
import com.spammers.AlertsAndNotifications.model.enums.EmailTemplate;

public interface EmailService {
    void sendEmailCustomised(String to, String subject, String body) throws SpammersPrivateExceptions;
    void sendEmailTemplate(String to, EmailTemplate template, Object... args) throws SpammersPrivateExceptions;
}
