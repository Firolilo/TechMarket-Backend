package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record PricingSuggestionRequest(
        @NotBlank(message = "servicio is required") String servicio, BigDecimal precioActual) {}
