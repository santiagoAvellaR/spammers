package com.spammers.AlertsAndNotifications.exceptions;

public class SpammersPrivateExceptions extends RuntimeException {
    public static final String EMAIL_EXCEPTION="THERE HAS BEEN AN ERROR WITH THE EMAIL";
    public SpammersPrivateExceptions(String message) {
        super(message);
    }
}
