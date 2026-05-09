package com.techmarket.iamservice.application.dto;

public record AuthUserSummaryResponse(
        String id, String email, String nombre, String tipo, String estado) {}
