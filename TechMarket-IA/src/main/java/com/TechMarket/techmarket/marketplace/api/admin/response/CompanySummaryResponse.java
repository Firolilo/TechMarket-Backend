package com.techmarket.techmarket.marketplace.api.admin.response;

public record CompanySummaryResponse(
        String id,
        String nombre,
        String logo,
        double calificacion,
        String descripcion,
        String tipo) {}
