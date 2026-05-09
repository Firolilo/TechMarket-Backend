package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record RegisterRequest(
        @Size(min = 3, max = 100, message = "username must have 3-100 chars") String username,
        @Email(message = "email is invalid") String email,
        @Size(min = 8, max = 72, message = "password must have 8-72 chars") String password,
        String confirmPassword,
        String tipo,
        String nombre,
        String apellido,
        String telefono,
        String pais,
        String ciudad,
        Boolean terminos,
        Boolean otpEnabled) {

    public String effectiveUsername() {
        if (username != null && !username.isBlank()) {
            return username;
        }
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public boolean endpointContract() {
        return tipo != null
                || nombre != null
                || apellido != null
                || telefono != null
                || pais != null
                || ciudad != null
                || terminos != null
                || confirmPassword != null;
    }
}
