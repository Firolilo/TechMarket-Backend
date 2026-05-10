package com.techmarket.techmarket.specialists.api.admin.response;

public record PricingSuggestionResponse(
        String servicio, String precioActual, PricingSuggestionDetailResponse sugerencia) {}
