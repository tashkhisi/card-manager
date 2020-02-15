package com.digipay.cardmanager.controller;

import com.digipay.cardmanager.data.ClientError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GeneralControllerAdvice
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = { IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ClientError("", ex.getMessage()));

    }
}
