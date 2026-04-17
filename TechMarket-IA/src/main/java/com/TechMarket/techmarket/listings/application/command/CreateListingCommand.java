package com.techmarket.techmarket.listings.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateListingCommand(
        UUID tenantId,
        UUID categoryId,
        UUID brandId,
        String title,
        BigDecimal basePrice,
        String currency,
        String status) {}
