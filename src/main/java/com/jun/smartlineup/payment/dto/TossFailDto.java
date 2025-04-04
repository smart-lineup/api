package com.jun.smartlineup.payment.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TossFailDto {
    private String version;

    private String traceId;

    private ErrorDetail error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ErrorDetail {
        private String code;
        private String message;
    }
}
