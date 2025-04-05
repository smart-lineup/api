package com.jun.smartlineup.exception;

public class NotAvailableRefundException extends RuntimeException {
    public NotAvailableRefundException() {
        super("Cannot refund due to policy");
    }
}
