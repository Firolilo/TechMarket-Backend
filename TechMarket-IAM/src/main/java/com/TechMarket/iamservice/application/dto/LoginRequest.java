package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Locale;

public record LoginRequest(
        String username,
        String email,
        @NotBlank(message = "password is required") String password,
        String tipo) {

    public String loginIdentifier() {
        if (username != null && !username.isBlank()) {
            return username.trim();
        }
        if (email != null && !email.isBlank()) {
            return email.trim().toLowerCase(Locale.ROOT);
        }
        return null;
    }

    public boolean endpointContract() {
        return email != null || tipo != null;
    }
}
