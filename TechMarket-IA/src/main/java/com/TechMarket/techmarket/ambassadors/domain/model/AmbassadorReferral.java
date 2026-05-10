package com.techmarket.techmarket.ambassadors.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AmbassadorReferral(
        UUID id,
        UUID ambassadorId,
        UUID tenantId,
        String attributionChannel,
        String usedCode,
        String status,
        OffsetDateTime createdAt) {}
