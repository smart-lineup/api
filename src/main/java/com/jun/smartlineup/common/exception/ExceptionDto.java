package com.jun.smartlineup.common.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionDto {
    private int code;
    private String status;
    private String message;
}
