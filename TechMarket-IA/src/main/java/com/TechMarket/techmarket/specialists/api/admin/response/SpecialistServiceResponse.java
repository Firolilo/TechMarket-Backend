package com.techmarket.techmarket.specialists.api.admin.response;

public record SpecialistServiceResponse(
        String id,
        String nombre,
        String descripcion,
        String precio,
        String tipo,
        boolean destacado) {}
