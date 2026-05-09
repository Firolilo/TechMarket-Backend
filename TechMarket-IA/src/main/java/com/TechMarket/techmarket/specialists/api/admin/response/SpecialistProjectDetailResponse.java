package com.techmarket.techmarket.specialists.api.admin.response;

public record SpecialistProjectDetailResponse(
        String id,
        SpecialistProjectClientResponse cliente,
        String servicio,
        String fecha,
        String estado,
        String descripcion) {}
