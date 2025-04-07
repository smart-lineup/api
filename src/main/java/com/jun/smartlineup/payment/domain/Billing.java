package com.jun.smartlineup.payment.domain;

import com.jun.smartlineup.payment.dto.BillingIssueKeyResponseDto;
import com.jun.smartlineup.payment.dto.PaymentInfoAddDto;
import com.jun.smartlineup.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String billingKey;

    private String customerKey;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BillingStatus status = BillingStatus.NONE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentProvider = PaymentMethod.TOSS;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    private LocalDate startedAt;

    private LocalDate endedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean renewal = false;

    private BigDecimal price;

    @Column(length = 4)
    private String cardLastNumber;

    @Builder.Default
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void changeInfo(PaymentInfoAddDto dto) {
        this.price = dto.getPlanType().getPrice();
        this.planType = dto.getPlanType();
    }

    public void issueKey(BillingIssueKeyResponseDto dto) {
        this.billingKey = dto.getBillingKey();
        this.customerKey = dto.getCustomerKey();
        this.cardLastNumber = dto.getCardNumber().substring(dto.getCardNumber().length() - 4);
        this.paymentProvider = PaymentMethod.TOSS;
        this.status = BillingStatus.NONE;
    }

    public void subscribe() {
        LocalDate today = LocalDate.now();
        if (endedAt == null || endedAt.isBefore(LocalDate.now())) {
            startedAt = today;
            endedAt = today.plusMonths(planType.getMonth());
        }
        status = BillingStatus.ACTIVE;
        renewal = true;
    }

    public void changePlanType(PlanType planType) {
        this.planType = planType;
        this.renewal = true;
    }

    public void refund() {
        status = BillingStatus.CANCEL;
        renewal = false;
        endedAt = LocalDate.now().minusDays(1);
    }

    public void cancel() {
        renewal = false;
    }

    public void batchFail() {
        status = BillingStatus.EXPIRED;
        renewal = false;
    }
}
