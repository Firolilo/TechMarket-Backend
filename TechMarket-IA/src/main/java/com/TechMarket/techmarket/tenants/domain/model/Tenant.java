package com.techmarket.techmarket.tenants.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Tenant(
        UUID id,
        String businessName,
        String legalName,
        String taxId,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}
