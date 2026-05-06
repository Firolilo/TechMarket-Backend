package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Locale;

public record LoginRequest(
        @NotBlank(message = "{validation.user.username.required}") String username,
        @NotBlank(message = "{validation.user.password.required}") String password) {}
