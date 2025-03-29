package com.jun.smartlineup.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentPayRequestDto {
    private String customerKey;
    private Long amount;
    private String orderId;
    private String orderName;
    private Integer cardInstallmentPlan = 1;
    private String customerEmail;
    private String customerName;
    private Long taxFreeAmount;
}
