package com.jun.smartlineup.exception;

import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> badRequestException(RuntimeException e) {
        System.out.println(e.getMessage());
        return ResponseEntity.badRequest().body("badRequestException");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ExceptionDto> emailAlreadyExistException(EmailAlreadyExistException e) {
        System.out.println(e.getMessage());
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
        System.out.println(e.getMessage());
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
        System.out.println(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
