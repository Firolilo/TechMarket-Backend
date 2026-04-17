package com.techmarket.iamservice.api.exception;

import com.techmarket.core.shared.exceptions.EntityNotFoundException;
import com.techmarket.core.shared.exceptions.TechMarketException;
import com.techmarket.iamservice.api.exception.dto.ApiErrorResponse;
import com.techmarket.iamservice.application.exception.AuthServiceException;
import com.techmarket.iamservice.application.exception.IamServiceException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TechMarketException.class)
    public ResponseEntity<ApiErrorResponse> handleTechMarketException(
            TechMarketException ex, HttpServletRequest request) {
        HttpStatus status = determineHttpStatus(ex);

        log.error(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ex.getCode(),
                status.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(ex.getCode(), Instant.now(), request.getRequestURI());

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IamServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleIamServiceException(
            IamServiceException ex, HttpServletRequest request) {
        log.error(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ex.getErrorCode(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(ex.getErrorCode(), Instant.now(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthServiceException(
            AuthServiceException ex, HttpServletRequest request) {
        log.warn(
                "event=IAM_AUTH_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ex.getErrorCode(),
                ex.getStatus().value(),
                request.getRequestURI());

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(ex.getErrorCode(), Instant.now(), request.getRequestURI());

        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(
                        error -> {
                            fieldErrors.put(error.getField(), error.getDefaultMessage());
                        });

        log.warn(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ErrorCodes.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        ErrorCodes.VALIDATION_ERROR,
                        Instant.now(),
                        request.getRequestURI(),
                        fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ErrorCodes.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        ErrorCodes.VALIDATION_ERROR,
                        Instant.now(),
                        request.getRequestURI(),
                        Map.of(
                                "message",
                                ex.getMessage() != null ? ex.getMessage() : "Bad request"));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ErrorCodes.FORBIDDEN,
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        ErrorCodes.FORBIDDEN,
                        Instant.now(),
                        request.getRequestURI(),
                        Map.of(
                                "message",
                                ex.getMessage() != null ? ex.getMessage() : "Access denied"));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {
        log.error(
                "event=IAM_API_EXCEPTION_HANDLED errorCode={} status={} path={}",
                ErrorCodes.UNEXPECTED_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(),
                ex);

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        ErrorCodes.UNEXPECTED_ERROR, Instant.now(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private HttpStatus determineHttpStatus(TechMarketException ex) {
        if (ex instanceof EntityNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
