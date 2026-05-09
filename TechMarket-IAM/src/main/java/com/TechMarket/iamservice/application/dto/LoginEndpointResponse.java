package com.techmarket.iamservice.application.dto;

public record LoginEndpointResponse(
        AuthUserSummaryResponse usuario, String token, String refreshToken, long expiresIn) {}
