package com.jun.smartlineup.payment.dto;

import com.jun.smartlineup.payment.domain.PayStatus;
import com.jun.smartlineup.payment.domain.PaymentTransaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentHistoryDto {
    private BigDecimal amount;
    private PayStatus status;
    private String method;
    private LocalDateTime createdAt;

    public static PaymentHistoryDto fromDto(PaymentTransaction pt) {
        return PaymentHistoryDto.builder()
                .amount(pt.getAmount())
                .status(pt.getStatus())
                .method("CARD")
                .createdAt(pt.getCreatedAt())
                .build();
    }
}
