package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(
        @NotBlank(message = "username is required") String username,
        @NotBlank(message = "otpChallengeId is required") String otpChallengeId,
        @NotBlank(message = "otpCode is required") String otpCode) {}
