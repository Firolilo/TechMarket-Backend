package com.techmarket.techmarket.ambassadors.api.admin.response;

public record AmbassadorHistoryPeriodResponse(
        String periodo,
        double impactoEconomico,
        double ingresoEmbajador,
        String estado,
        String fechaPago) {}
