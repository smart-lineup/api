package com.jun.smartlineup.common;

import com.jun.smartlineup.common.exception.*;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> badRequestException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body("badRequestException");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ExceptionDto> emailAlreadyExistException(EmailAlreadyExistException e) {
        log.error(e.getMessage(), e);
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .code(400)
                .status("email")
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(exceptionDto);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotVerifyUserException.class)
    public ResponseEntity<ExceptionDto> notVerifyUserException(NotVerifyUserException e) {
        log.error(e.getMessage(), e);
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .code(400)
                .status("not_verify")
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(exceptionDto);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<String> messagingException(MessagingException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResetEmailSnsLoginException.class)
    public ResponseEntity<String> resetEmailSnsLoginException(ResetEmailSnsLoginException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DuplicationAttendeeException.class)
    public ResponseEntity<String> duplicationAttendeeException(DuplicationAttendeeException e) {
        return ResponseEntity.ok(e.getMessage());
    }
}
