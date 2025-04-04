package com.jun.smartlineup.payment.dto;

import com.jun.smartlineup.payment.domain.PlanType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BillingPlanTypeRequestDto {
    @NotNull
    private PlanType planType;
}
