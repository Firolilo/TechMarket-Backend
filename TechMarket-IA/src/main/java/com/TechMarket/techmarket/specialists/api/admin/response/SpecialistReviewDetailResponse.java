package com.techmarket.techmarket.specialists.api.admin.response;

public record SpecialistReviewDetailResponse(
        String id,
        String cliente,
        int estrellas,
        String comentario,
        String respuestaTecnico,
        String servicio,
        String fecha) {}
