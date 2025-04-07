package com.jun.smartlineup.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum PlanType {
    ANNUAL("ANNUAL", "연간", 12, BigDecimal.valueOf(99000)),
    MONTHLY("MONTHLY", "월간", 1, BigDecimal.valueOf(9900));

    private final String key;
    private final String korean;
    private final int month;
    private final BigDecimal price;
}
