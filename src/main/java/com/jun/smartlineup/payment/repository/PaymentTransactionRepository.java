package com.jun.smartlineup.payment.repository;

import com.jun.smartlineup.payment.dao.PaymentHistoryRow;
import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.PaymentTransaction;
import com.jun.smartlineup.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    @Query("""
            SELECT new com.jun.smartlineup.payment.dao.PaymentHistoryRow(
                t.amount,
                t.paymentMethod,
                t.createdAt,
                CASE
                    WHEN c.id IS NOT NULL THEN 'REFUND'
                    ELSE t.status
                END)
            FROM PaymentTransaction t LEFT JOIN PaymentCancel c on c.paymentTransaction = t WHERE t.user = :user
            ORDER BY t.createdAt DESC
            """)
    List<PaymentHistoryRow> findByUserWithCancel(User user);

    Optional<PaymentTransaction> findFirstByUserOrderByCreatedAtDesc(User user);

    boolean existsByBillingAndCreatedAtBetween(Billing billing, LocalDateTime start, LocalDateTime end);
}
