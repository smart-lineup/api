package com.jun.smartlineup.payment.domain;


import com.jun.smartlineup.payment.dto.TossErrorResponse;
import com.jun.smartlineup.payment.dto.TossPaymentResponseDto;
import com.jun.smartlineup.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", nullable = false)
    private Billing billing;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private String transactionId;

    private String orderId;

    private String paymentKey;

    private String mid;

    private String receiptUrl;

    @Enumerated(EnumType.STRING)
    private PayStatus status;

    private String failCode;
    private String failMessage;

    @Builder.Default
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static PaymentTransaction payWithToss(Billing billing, TossPaymentResponseDto dto) {
        return PaymentTransaction.builder()
                .user(billing.getUser())
                .billing(billing)
                .amount(billing.getPrice())
                .paymentMethod(PaymentMethod.TOSS)
                .orderId(dto.getOrderId())
                .paymentKey(dto.getPaymentKey())
                .mid(dto.getMId())
                .receiptUrl(dto.getReceiptUrl())
                .status(PayStatus.PAID)
                .build();
    }

    public static PaymentTransaction payFailWithToss(Billing billing, TossErrorResponse errorResponse) {
        return PaymentTransaction.builder()
                .user(billing.getUser())
                .billing(billing)
                .amount(billing.getPrice())
                .paymentMethod(PaymentMethod.TOSS)
                .status(PayStatus.FAIL)
                .failCode(errorResponse.getCode())
                .failMessage(errorResponse.getMessage())
                .build();
    }
}
