package com.jun.smartlineup.exception;

import com.jun.smartlineup.payment.dto.TossFailDto;

public class TossApiException extends RuntimeException {
    private final TossFailDto error;

    public TossApiException(TossFailDto error) {
        super("Toss API Error: " + error.getError().getCode());
        this.error = error;
    }

    public TossFailDto getError() {
        return error;
    }
}
