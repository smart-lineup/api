package com.jun.smartlineup.payment.dao;

import com.jun.smartlineup.payment.domain.PayStatus;
import com.jun.smartlineup.payment.domain.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentHistoryRow(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt,
        PayStatus status
) {}
