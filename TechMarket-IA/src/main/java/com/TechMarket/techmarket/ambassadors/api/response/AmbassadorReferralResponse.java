package com.techmarket.techmarket.ambassadors.api.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AmbassadorReferralResponse(
        UUID id,
        UUID ambassadorId,
        UUID tenantId,
        String attributionChannel,
        String usedCode,
        String status,
        OffsetDateTime createdAt) {}
