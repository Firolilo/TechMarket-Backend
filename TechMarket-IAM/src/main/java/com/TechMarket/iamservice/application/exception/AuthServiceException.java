package com.techmarket.iamservice.application.exception;

import org.springframework.http.HttpStatus;

public class AuthServiceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus status;

    public AuthServiceException(String errorCode, HttpStatus status, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
