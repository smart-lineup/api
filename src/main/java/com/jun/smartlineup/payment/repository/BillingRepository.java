package com.jun.smartlineup.payment.repository;

import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    Optional<Billing> getBillingByUser(User user);
}
