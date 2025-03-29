package com.jun.smartlineup.payment.dto;

import com.jun.smartlineup.payment.domain.PlanType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentInfoDto {
    @NotNull
    private Long price;
    @NotNull
    private PlanType planType;
}
