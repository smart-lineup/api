package com.jun.smartlineup.common.exception;

import com.jun.smartlineup.payment.domain.Billing;

public class BatchTryDoublePayException extends RuntimeException {
    public BatchTryDoublePayException(Billing billing) {
        super("오늘 이미 결제된 Billing - billingId: " + billing.getId());
    }
}
