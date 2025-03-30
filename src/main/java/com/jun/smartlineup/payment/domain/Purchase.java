package com.jun.smartlineup.payment.domain;


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

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", nullable = false)
    private Billing billing;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, length = 255)
    private String transactionId;

    private String paymentKey;

    private String mid;

    @Enumerated(EnumType.STRING)
    private PayStatus status;

    @Builder.Default
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static Purchase successWithToss(User user, Billing billing, TossPaymentResponseDto dto) {
        return Purchase.builder()
                .user(user)
                .billing(billing)
                .amount(billing.getPrice())
                .paymentMethod(PaymentMethod.TOSS)
                .transactionId(dto.getOrderId())
                .paymentKey(dto.getPaymentKey())
                .mid(dto.getMId())
                .status(PayStatus.SUCCESS)
                .build();
    }
}
