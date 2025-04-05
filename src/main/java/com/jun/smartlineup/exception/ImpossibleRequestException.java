package com.jun.smartlineup.exception;

import com.jun.smartlineup.user.domain.User;

public class ImpossibleRequestException extends RuntimeException {
    public ImpossibleRequestException(String functionName, User user) {
        super("Impossible request::" + functionName + "::user=" + user.getEmail());
    }

    public ImpossibleRequestException(String functionName) {
        super("Impossible request::" + functionName);
    }
}
