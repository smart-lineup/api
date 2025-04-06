package com.jun.smartlineup.payment.dto;

import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.BillingStatus;
import com.jun.smartlineup.payment.domain.PlanType;
import com.jun.smartlineup.payment.util.PaymentUtil;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PaymentInfoResponseDto {
    private Boolean isExist;
    private Boolean isSubscribe;
    private String cardLastNumber;
    private LocalDate endAt;
    private PlanType planType;
    private BillingStatus status;
    private LocalDate nextPaymentDate;
    private Boolean isRefundable;

    public static PaymentInfoResponseDto exist(Billing billing) {
        return PaymentInfoResponseDto.builder()
                .isExist(true)
                .cardLastNumber(billing.getCardLastNumber())
                .endAt(billing.getEndedAt())
                .isSubscribe(billing.getEndedAt().isAfter(LocalDate.now()))
                .planType(billing.getPlanType())
                .status(billing.getStatus())
                .nextPaymentDate(getNextPaymentDate(billing))
                .isRefundable(PaymentUtil.isRefundable(billing))
                .build();
    }

    private static LocalDate getNextPaymentDate(Billing billing) {
        if (!billing.getRenewal()) {
            return null;
        }
        return billing.getEndedAt().plusDays(1);
    }

    public static PaymentInfoResponseDto notExist() {
        return PaymentInfoResponseDto.builder()
                .isExist(false)
                .build();
    }
}
