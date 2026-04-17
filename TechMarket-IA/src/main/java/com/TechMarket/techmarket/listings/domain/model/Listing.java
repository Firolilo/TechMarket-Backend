package com.techmarket.techmarket.listings.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record Listing(
        UUID id,
        UUID tenantId,
        UUID categoryId,
        UUID brandId,
        String title,
        BigDecimal basePrice,
        String currency,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}
