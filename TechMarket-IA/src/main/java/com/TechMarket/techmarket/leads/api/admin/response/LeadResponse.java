package com.techmarket.techmarket.leads.api.admin.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LeadResponse(
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
