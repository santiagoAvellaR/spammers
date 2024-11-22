package com.spammers.AlertsAndNotifications.exceptions;

public class SpammersPublicExceptions extends RuntimeException {
    public static final String WRONG_FINE_RATE = "The rate must be greater than 0";
    public static final String FINE_PENDING = "There is a Fine associated with loan, it can't be closed until the fine is payed.";

    public SpammersPublicExceptions(String message) {
        super(message);
    }
}
