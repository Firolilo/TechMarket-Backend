package com.techmarket.techmarket.leads.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Lead(
        UUID id,
        UUID tenantId,
        UUID branchId,
        UUID userId,
        UUID listingId,
        String sourceChannel,
        String status,
        Integer leadScore,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}
