package com.spammers.AlertsAndNotifications.exceptions;

public class SpammersPublicExceptions extends RuntimeException {
    public static final String WRONG_FINE_RATE = "The rate must be greater than 0";

    public SpammersPublicExceptions(String message) {
        super(message);
    }
}
