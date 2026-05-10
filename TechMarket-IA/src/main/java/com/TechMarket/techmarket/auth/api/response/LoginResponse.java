package com.techmarket.techmarket.auth.api.response;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email,
        String firstName,
        String lastName) {}
