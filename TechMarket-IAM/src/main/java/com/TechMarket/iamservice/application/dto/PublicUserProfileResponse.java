package com.techmarket.iamservice.application.dto;

public record PublicUserProfileResponse(
        String id,
        String nombre,
        String apellido,
        String tipo,
        String ciudad,
        String avatar,
        boolean verificado,
        double reputacion,
        long totalResenas) {}
