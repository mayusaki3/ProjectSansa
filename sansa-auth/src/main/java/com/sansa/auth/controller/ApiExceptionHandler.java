package com.sansa.auth.controller;

import com.sansa.auth.dto.common.ProblemDetail;
import com.sansa.auth.service.error.InvalidCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvalidCodeException.class)
    public org.springframework.http.ResponseEntity<ProblemDetail> handleInvalidCode(InvalidCodeException ex) {
        ProblemDetail body = ProblemDetail.builder()
                .type("about:blank")
                .title("Invalid verification code")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(ex.getMessage())
                .instance(null)
                .build();
        return new org.springframework.http.ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
