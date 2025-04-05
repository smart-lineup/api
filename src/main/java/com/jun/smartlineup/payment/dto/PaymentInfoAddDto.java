package com.jun.smartlineup.payment.dto;

import com.jun.smartlineup.payment.domain.PlanType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentInfoAddDto {
    @NotNull
    private BigDecimal price;
    @NotNull
    private PlanType planType;
}
