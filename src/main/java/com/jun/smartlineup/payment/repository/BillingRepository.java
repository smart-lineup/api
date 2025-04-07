package com.jun.smartlineup.payment.repository;

import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.BillingStatus;
import com.jun.smartlineup.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    Optional<Billing> getBillingByUser(User user);

    @Query("SELECT b FROM Billing b WHERE b.endedAt < :today AND b.status = :status")
    List<Billing> findAllActiveBeforeToday(LocalDate today, BillingStatus status);
}
