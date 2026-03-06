package com.richards.projectmanagement.common.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email) {
        super("Email already in use: " + email);
    }
}
