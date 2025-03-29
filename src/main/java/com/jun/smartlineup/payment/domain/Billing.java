package com.jun.smartlineup.payment.domain;

import com.jun.smartlineup.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 255)
    private String billingKey;  // Toss에서 받은 billingKey

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BillingStatus status = BillingStatus.ACTIVE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentProvider = PaymentMethod.TOSS;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @Column(nullable = false)
    private Boolean renewal = true;

    @Column(nullable = false)
    private Integer price;

    @Builder.Default
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();
}
