package com.techmarket.techmarket.ambassadors.api.admin.response;

public record AmbassadorPayoutResponse(
        String id,
        double monto,
        String metodo,
        String estado,
        String fechaSolicitada,
        String fechaPago) {}
