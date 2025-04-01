package com.jun.smartlineup.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentPayRequestDto {
    private String customerKey;
    private BigDecimal amount;
    private String orderId;
    private String orderName;
    private Integer cardInstallmentPlan = 1;
    private String customerEmail;
    private String customerName;
    private BigDecimal taxFreeAmount;
}
