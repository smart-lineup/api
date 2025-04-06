package com.jun.smartlineup.payment.util;

import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.domain.BillingStatus;

import java.time.LocalDate;

public class PaymentUtil {
    public static boolean isRefundable(Billing billing) {
        if (billing.getStatus() != BillingStatus.ACTIVE) {
            return false;
        }
        LocalDate refundDeadline = billing.getStartedAt().plusDays(1);
        return !LocalDate.now().isAfter(refundDeadline);
    }
}
