package com.jun.smartlineup.payment.repository;

import com.jun.smartlineup.payment.domain.PaymentCancel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, Long> {

}
