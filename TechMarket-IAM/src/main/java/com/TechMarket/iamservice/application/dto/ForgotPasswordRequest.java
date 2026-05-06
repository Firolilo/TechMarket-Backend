package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "email is required") @Email(message = "email is invalid")
                String email) {}
