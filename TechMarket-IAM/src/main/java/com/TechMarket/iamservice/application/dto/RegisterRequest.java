package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username is required")
                @Size(min = 3, max = 100, message = "username must have 3-100 chars")
                String username,
        @NotBlank(message = "email is required") @Email(message = "email is invalid") String email,
        @NotBlank(message = "password is required")
                @Size(min = 8, max = 72, message = "password must have 8-72 chars")
                String password,
        Boolean otpEnabled) {}
