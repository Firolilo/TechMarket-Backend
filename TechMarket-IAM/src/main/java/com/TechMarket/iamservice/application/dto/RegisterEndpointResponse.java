package com.techmarket.iamservice.application.dto;

public record RegisterEndpointResponse(
        String id, String email, String nombre, String tipo, String estado, String mensaje) {}
