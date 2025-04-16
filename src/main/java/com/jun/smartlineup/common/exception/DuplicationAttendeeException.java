package com.jun.smartlineup.common.exception;

public class DuplicationAttendeeException extends RuntimeException {
    public DuplicationAttendeeException() {
        super("이미 라인에 존재하는 유저입니다.");
    }
}
