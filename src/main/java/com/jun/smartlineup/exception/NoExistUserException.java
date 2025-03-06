package com.jun.smartlineup.exception;

public class NoExistUserException extends RuntimeException {
    public NoExistUserException() {
        super("no exist user");
    }
}
