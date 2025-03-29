package com.jun.smartlineup.payment.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class TossPaymentResponseDto {
    private String version;
    private String paymentKey;
    private String type;
    private String orderId;
    private String orderName;
    private String mId;
    private String currency;
    private String method;
    private int totalAmount;
    private int balanceAmount;
    private String status;
    private ZonedDateTime requestedAt;
    private ZonedDateTime approvedAt;
    private boolean useEscrow;
    private String lastTransactionKey;
    private int suppliedAmount;
    private int vat;
    private boolean cultureExpense;
    private int taxFreeAmount;
    private int taxExemptionAmount;
    private boolean isPartialCancelable;
    private Card card;
    private List<Cancel> cancels;
    private VirtualAccount virtualAccount;
    private String refundStatus;
    private boolean expired;
    private String settlementStatus;
    private RefundReceiveAccount refundReceiveAccount;
    private String secret;
    private MobilePhone mobilePhone;
    private String receiptUrl;
    private GiftCertificate giftCertificate;
    private Transfer transfer;
    private Metadata metadata;
    private Receipt receipt;
    private Checkout checkout;
    private EasyPay easyPay;
    private String country;
    private Failure failure;
    private CashReceipt cashReceipt;
    private List<CashReceiptHistory> cashReceipts;
    private Discount discount;

    @Data
    public static class Card {
        private int amount;
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private int installmentPlanMonths;
        private String approveNo;
        private boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private boolean isInterestFree;
        private String interestPayer;
    }

    @Data
    public static class Cancel {
        private int cancelAmount;
        private String cancelReason;
        private int taxFreeAmount;
        private int taxExemptionAmount;
        private int refundableAmount;
        private int transferDiscountAmount;
        private int easyPayDiscountAmount;
        private ZonedDateTime canceledAt;
        private String transactionKey;
        private String receiptKey;
        private String cancelStatus;
        private String cancelRequestId;
    }

    @Data
    public static class VirtualAccount {
        private String accountType;
        private String accountNumber;
        private String bankCode;
        private String customerName;
        private ZonedDateTime dueDate;
    }

    @Data
    public static class RefundReceiveAccount {
        private String bankCode;
        private String accountNumber;
        private String holderName;
    }

    @Data
    public static class MobilePhone {
        private String customerMobilePhone;
    }

    @Data
    public static class GiftCertificate {
        private String approveNo;
        private String settlementStatus;
    }

    @Data
    public static class Transfer {
        private String bankCode;
        private String settlementStatus;
    }

    @Data
    public static class Metadata {
        // 키-값으로 관리하는 map 구조로도 가능
    }

    @Data
    public static class Receipt {
        private String url;
    }

    @Data
    public static class Checkout {
        private String url;
    }

    @Data
    public static class EasyPay {
        private String provider;
        private int amount;
        private int discountAmount;
    }

    @Data
    public static class Failure {
        private String code;
        private String message;
    }

    @Data
    public static class CashReceipt {
        private String type;
        private String receiptKey;
        private String issueNumber;
        private String receiptUrl;
        private int amount;
        private int taxFreeAmount;
    }

    @Data
    public static class CashReceiptHistory {
        private String receiptKey;
        private String orderId;
        private String orderName;
        private String type;
        private String issueNumber;
        private String receiptUrl;
        private String businessNumber;
        private String transactionType;
        private int amount;
        private int taxFreeAmount;
        private String issueStatus;
        private Failure failure;
        private String customerIdentityNumber;
        private ZonedDateTime requestedAt;
    }

    @Data
    public static class Discount {
        private int amount;
    }
}

