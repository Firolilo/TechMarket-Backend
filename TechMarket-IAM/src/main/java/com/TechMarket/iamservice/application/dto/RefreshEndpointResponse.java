package com.techmarket.iamservice.application.dto;

public record RefreshEndpointResponse(String token, String refreshToken, long expiresIn) {}
