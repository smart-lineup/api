package com.jun.smartlineup.common.exception;

public class NoExistUserException extends RuntimeException {
    public NoExistUserException() {
        super("no exist user");
    }
}
