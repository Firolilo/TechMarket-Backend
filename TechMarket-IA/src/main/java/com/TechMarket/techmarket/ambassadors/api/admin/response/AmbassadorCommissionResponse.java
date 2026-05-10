package com.techmarket.techmarket.ambassadors.api.admin.response;

public record AmbassadorCommissionResponse(
        String id,
        String fecha,
        String eventType,
        String referenceType,
        String descripcion,
        double monto,
        double impactoGenerado,
        String estado,
        boolean confirmado,
        int nivel) {}
