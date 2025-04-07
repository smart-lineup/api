package com.jun.smartlineup.common.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException() {
        super("The email address already exists.");
    }
}
