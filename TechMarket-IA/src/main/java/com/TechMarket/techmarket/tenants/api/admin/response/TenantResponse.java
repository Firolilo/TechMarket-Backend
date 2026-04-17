package com.techmarket.techmarket.tenants.api.admin.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String businessName,
        String legalName,
        String taxId,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}
