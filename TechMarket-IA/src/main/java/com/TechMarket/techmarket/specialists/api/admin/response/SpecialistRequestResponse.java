package com.techmarket.techmarket.specialists.api.admin.response;

public record SpecialistRequestResponse(
        String id, String cliente, String servicio, String fecha, String estado, String urgencia) {}
