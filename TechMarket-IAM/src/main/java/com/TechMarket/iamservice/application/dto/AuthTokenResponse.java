package com.techmarket.iamservice.application.dto;

import java.util.List;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        long refreshExpiresIn,
        String tenantId,
        Long userId,
        String username,
        List<String> roles,
        List<String> scopes) {}
