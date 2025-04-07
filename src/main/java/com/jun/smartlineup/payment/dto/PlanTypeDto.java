package com.jun.smartlineup.payment.dto;

import com.jun.smartlineup.payment.domain.PlanType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PlanTypeDto {
    private PlanDetails monthly;
    private PlanDetails annual;

    record PlanDetails(String korean, int month, BigDecimal price) {}

    public static PlanTypeDto create() {
        return PlanTypeDto.builder()
                .monthly(from(PlanType.MONTHLY))
                .annual(from(PlanType.ANNUAL))
                .build();
    }

    private static PlanDetails from(PlanType planType) {
        return new PlanDetails(
                planType.getKorean(),
                planType.getMonth(),
                planType.getPrice()
        );
    }
}
