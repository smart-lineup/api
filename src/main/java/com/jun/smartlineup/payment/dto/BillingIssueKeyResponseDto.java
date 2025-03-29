package com.jun.smartlineup.payment.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class BillingIssueKeyResponseDto {
    private String mId;
    private String customerKey;
    private ZonedDateTime authenticatedAt;
    private String method;
    private String billingKey;

    private CardInfo card;          // 하위 객체
    private String cardCompany;
    private String cardNumber;

    @Data
    public static class CardInfo {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private String cardType;
        private String ownerType;
    }
}