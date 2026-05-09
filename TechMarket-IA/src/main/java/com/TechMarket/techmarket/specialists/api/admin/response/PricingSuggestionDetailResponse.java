package com.techmarket.techmarket.specialists.api.admin.response;

public record PricingSuggestionDetailResponse(
        String precioRecomendado, PricingRangeResponse rangoOptimo, String justificacion) {}
