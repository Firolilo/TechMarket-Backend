package com.techmarket.techmarket.listings.api.admin.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateListingRequest(
        @NotNull UUID tenantId,
        @NotNull UUID categoryId,
        @NotNull UUID brandId,
        @NotBlank String title,
        @NotNull @DecimalMin("0.0") BigDecimal basePrice,
        @NotBlank String currency,
        @NotBlank String status) {}
