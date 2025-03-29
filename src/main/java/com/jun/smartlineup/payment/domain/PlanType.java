package com.jun.smartlineup.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlanType {
    ANNUAL("ANNUAL", "연간", 12),
    MONTHLY("MONTHLY", "월간", 1);

    private final String key;
    private final String korean;
    private final int month;
}
