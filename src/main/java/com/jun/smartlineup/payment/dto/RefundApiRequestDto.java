package com.jun.smartlineup.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundApiRequestDto {
    private String cancelReason;
}
