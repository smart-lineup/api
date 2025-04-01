package com.jun.smartlineup.payment.dto;

public class ApiResult<T> {
    private final T data;
    private final TossFailDto error;

    private ApiResult(T data, TossFailDto error) {
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data, null);
    }

    public static <T> ApiResult<T> failure(TossFailDto error) {
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

    public TossFailDto getError() {
        return error;
    }
}
