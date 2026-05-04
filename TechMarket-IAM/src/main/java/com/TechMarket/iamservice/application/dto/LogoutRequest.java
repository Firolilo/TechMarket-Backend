package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "{validation.refreshToken.required}") String refreshToken) {}
