package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Locale;

public record LoginRequest(
        String username,
        String email,
        @NotBlank(message = "{validation.user.password.required}") String password,
        String tipo,
        String otpChallengeId,
        String otpCode) {

    public LoginRequest(String username, String password) {
        this(username, null, password, null, null, null);
    }

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
