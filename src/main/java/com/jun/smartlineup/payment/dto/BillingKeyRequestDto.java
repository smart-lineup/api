package com.jun.smartlineup.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillingKeyRequestDto {
    @NotBlank
    private String authKey;
    @NotBlank
    private String customerKey;
}
