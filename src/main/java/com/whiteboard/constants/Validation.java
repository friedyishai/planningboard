package com.whiteboard.constants;

public class Validation {

    public static final int
            MIN_USER_NAME_LEN = 3,
            MAX_USER_NAME_LEN = 25,
            MIN_PASSWORD_LEN = 4,
            MAX_PASSWORD_LEN = 100,
            MIN_EMAIL_LEN = 7,
            MAX_EMAIL_LEN = 45;

    public static final String
        EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Validation() {
    }
}
