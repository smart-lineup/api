package com.jun.smartlineup.payment.dto;

import lombok.ToString;

@ToString
public class ApiResult<T> {
    private final T data;
    private final TossErrorResponse error;

    private ApiResult(T data, TossErrorResponse error) {
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data, null);
    }

    public static <T> ApiResult<T> failure(TossErrorResponse error) {
        return new ApiResult<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFail() {
        return error != null;
    }

    public T getData() {
        return data;
    }

    public TossErrorResponse getError() {
        return error;
    }
}
