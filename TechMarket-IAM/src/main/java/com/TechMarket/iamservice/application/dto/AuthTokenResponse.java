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
        String tipo,
        List<String> roles,
        List<String> scopes,
        boolean otpRequired,
        String otpChallengeId,
        long otpExpiresIn) {

    public AuthTokenResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            long refreshExpiresIn,
            String tenantId,
            Long userId,
            String username,
            String tipo,
            List<String> roles,
            List<String> scopes) {
        this(
                accessToken,
                refreshToken,
                tokenType,
                expiresIn,
                refreshExpiresIn,
                tenantId,
                userId,
                username,
                tipo,
                roles,
                scopes,
                false,
                null,
                0);
    }
}
