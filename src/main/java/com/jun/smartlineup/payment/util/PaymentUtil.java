package com.jun.smartlineup.payment.util;

import java.time.LocalDate;

public class PaymentUtil {
    public static boolean isRefundable(LocalDate startedAt) {
        LocalDate refundDeadline = startedAt.plusDays(1);
        return !LocalDate.now().isAfter(refundDeadline);
    }
}
