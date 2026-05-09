package com.techmarket.techmarket.specialists.api.admin.response;

public record SpecialistTransactionDetailResponse(
        String id,
        String servicio,
        String cliente,
        String monto,
        String comisionPlataforma,
        String neto,
        String fecha,
        String estado) {}
