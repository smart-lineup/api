package com.jun.smartlineup.common.exception;

public class ResetEmailSnsLoginException extends RuntimeException {
    public ResetEmailSnsLoginException() {
        super("SNS 계정으로 로그인한 아이디입니다.");
    }
}
