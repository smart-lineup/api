package com.jun.smartlineup.payment.dto;

import com.jun.smartlineup.payment.dao.PaymentHistoryRow;
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

    public static PaymentHistoryDto fromDto(PaymentHistoryRow pt) {
        return PaymentHistoryDto.builder()
                .amount(pt.amount())
                .status(pt.status())
                .method("CARD")
                .createdAt(pt.createdAt())
                .build();
    }
}
