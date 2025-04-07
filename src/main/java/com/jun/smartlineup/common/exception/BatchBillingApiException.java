package com.jun.smartlineup.common.exception;

import com.jun.smartlineup.payment.domain.Billing;
import com.jun.smartlineup.payment.dto.TossErrorResponse;

public class BatchBillingApiException extends RuntimeException {
    public BatchBillingApiException(TossErrorResponse error, Billing billing) {
        super("Batch Billing::Toss API Error: " + error.getCode() + "::" + error.getMessage() + "::BillingId: " + billing.getId());
    }
}
