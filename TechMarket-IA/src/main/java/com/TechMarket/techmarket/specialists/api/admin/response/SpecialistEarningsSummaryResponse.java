package com.techmarket.techmarket.specialists.api.admin.response;

public record SpecialistEarningsSummaryResponse(
        String periodo, String total, long serviciosRealizados, String promedioPorServicio) {}
