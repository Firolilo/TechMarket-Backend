package com.techmarket.techmarket.specialists.api.admin.response;

public record SpecialistTransactionResponse(
        String id, String servicio, String cliente, String monto, String fecha, String estado) {}
