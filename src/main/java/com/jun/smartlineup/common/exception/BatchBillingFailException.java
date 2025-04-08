package com.jun.smartlineup.common.exception;

public class BatchBillingFailException extends RuntimeException {
    public BatchBillingFailException(String failedBillingIds) {
        super("Billing processing failed for billingIds: " + failedBillingIds);
    }
}
