package com.jun.smartlineup.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayResponseDto {
    private Boolean isSuccess;
    private String code;
    private String message;
}
