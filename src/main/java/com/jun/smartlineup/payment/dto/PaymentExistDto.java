package com.jun.smartlineup.payment.dto;

import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.BillingStatus;
import com.jun.smartlineup.payment.domain.PayStatus;
import com.jun.smartlineup.payment.domain.PlanType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PaymentExistDto {
    private Boolean isExist;
    private String cardLastNumber;
    private LocalDate endAt;
    private Boolean isSubscribe;
    private PlanType planType;
    private BillingStatus status;

    public static PaymentExistDto exist(Billing billing) {
        return PaymentExistDto.builder()
                .isExist(true)
                .cardLastNumber(billing.getCardLastNumber())
                .endAt(billing.getEndedAt())
                .isSubscribe(billing.getEndedAt().isAfter(LocalDate.now()))
                .planType(billing.getPlanType())
                .status(billing.getStatus())
                .build();
    }

    public static PaymentExistDto notExist() {
        return PaymentExistDto.builder()
                .isExist(false)
                .build();
    }
}
