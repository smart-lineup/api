package com.jun.smartlineup.payment.domain;

import com.jun.smartlineup.payment.dto.TossPaymentResponseDto;
import com.jun.smartlineup.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCancel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_cancel_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_transaction_id", nullable = false)
    private PaymentTransaction paymentTransaction;

    private LocalDateTime cancelAt;
    private String cancelReason;
    private BigDecimal cancelAmount;
    private String transactionKey;
    private String cancelStatus;
    private String receiptUrl;
    private String createdBy;
    private String memo;

    @Builder.Default
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static List<PaymentCancel> cancelWithToss(
            User user,
            PaymentTransaction transaction,
            TossPaymentResponseDto dto,
            String createdBy
    ) {
        return dto.getCancels().stream().map(cancel ->
                PaymentCancel.builder()
                        .user(user)
                        .paymentTransaction(transaction)
                        .cancelAt(cancel.getCanceledAt().toLocalDateTime())
                        .cancelAmount(BigDecimal.valueOf(cancel.getCancelAmount()))
                        .cancelReason(cancel.getCancelReason())
                        .transactionKey(cancel.getTransactionKey())
                        .cancelStatus(cancel.getCancelStatus())
                        .receiptUrl(cancel.getReceiptKey())
                        .createdBy(createdBy)
                        .memo("사용자 환불 처리")
                        .build()
        ).toList();
    }
}
