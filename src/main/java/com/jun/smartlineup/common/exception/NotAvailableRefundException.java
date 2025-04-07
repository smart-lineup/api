package com.jun.smartlineup.common.exception;

public class NotAvailableRefundException extends RuntimeException {
    public NotAvailableRefundException() {
        super("Cannot refund due to policy");
    }
}
