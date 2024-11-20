package com.spammers.AlertsAndNotifications.service.interfaces;

import com.spammers.AlertsAndNotifications.exceptions.SpammersPrivateExceptions;

public interface EmailService {
    void sendEmail(String to, String subject, String body) throws SpammersPrivateExceptions;
}
