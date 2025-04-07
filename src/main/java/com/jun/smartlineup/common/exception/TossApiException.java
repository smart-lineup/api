package com.jun.smartlineup.common.exception;

import com.jun.smartlineup.payment.dto.TossErrorResponse;

public class TossApiException extends RuntimeException {
    private final TossErrorResponse error;

    public TossApiException(TossErrorResponse error) {
        super("Toss API Error: " + error.getCode() + "::" + error.getMessage());
        this.error = error;
    }

    public TossErrorResponse getError() {
        return error;
    }
}
