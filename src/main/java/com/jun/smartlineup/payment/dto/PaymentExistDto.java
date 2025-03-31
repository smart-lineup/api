package com.jun.smartlineup.payment.dto;

import lombok.Data;

@Data
public class PaymentExistDto {
    private Boolean isExist;
    private String cardLastNumber;
}
