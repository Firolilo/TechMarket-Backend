package com.techmarket.techmarket.specialists.api.admin.response;

import java.math.BigDecimal;

public record SpecialistProfileStatsResponse(
        long trabajosCompletados, long totalResenas, BigDecimal calificacionPromedio) {}
