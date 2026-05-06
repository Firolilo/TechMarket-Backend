package com.techmarket.iamservice.api.rest.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String errorCode,
        String message,
        Instant timestamp,
        String path,
        Map<String, String> details) {

    public ErrorResponse(String errorCode, String message, Instant timestamp, String path) {
        this(errorCode, message, timestamp, path, null);
    }
}
