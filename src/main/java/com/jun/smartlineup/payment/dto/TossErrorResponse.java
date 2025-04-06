package com.jun.smartlineup.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossErrorResponse {
    private String version;
    private String traceId;
    private ErrorDetail error;

    private String code;
    private String message;

    public String getCode() {
        if (error != null && error.code != null) return error.code;
        return code;
    }

    public String getMessage() {
        if (error != null && error.message != null) return error.message;
        return message;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    public static class ErrorDetail {
        private String code;
        private String message;
    }
}
