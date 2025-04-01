package com.jun.smartlineup.payment.repository;

import com.jun.smartlineup.payment.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<PaymentTransaction, Long> {

}
