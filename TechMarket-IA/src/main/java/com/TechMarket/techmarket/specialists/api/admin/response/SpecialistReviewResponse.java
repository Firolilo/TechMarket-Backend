package com.techmarket.techmarket.specialists.api.admin.response;

public record SpecialistReviewResponse(
        String id,
        String cliente,
        int estrellas,
        String comentario,
        String servicio,
        String fecha,
        String respuestaTecnico) {}
