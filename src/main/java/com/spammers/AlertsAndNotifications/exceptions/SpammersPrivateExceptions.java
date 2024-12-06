package com.spammers.AlertsAndNotifications.exceptions;

public class SpammersPrivateExceptions extends RuntimeException {
    public static final String EMAIL_EXCEPTION="THERE HAS BEEN AN ERROR WITH THE EMAIL";
    public static final String LOAN_NOT_FOUND ="THE LOAN WAS NOT FOUND";
    public static final String USER_NOT_FOUND ="THE USER WAS NOT FOUND";
    public static final String FINE_NOT_FOUND ="THE FINE WAS NOT FOUND";
    public static final String ROLE_NOT_FOUND = "THE ROLE WAS NOT FOUND IN THE PAYLOAD OF THE TOKEN JWT";
    public int code;
    public static final String INVALID_RATE = "THE GIVEN RATE MUST BE IN RANGE [0,10000]";

    public SpammersPrivateExceptions(String message, int code) {
        super(message);
        this.code = code;
    }
}
