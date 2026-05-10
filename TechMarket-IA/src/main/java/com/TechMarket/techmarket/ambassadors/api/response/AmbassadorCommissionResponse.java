package com.techmarket.techmarket.ambassadors.api.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AmbassadorCommissionResponse(
        UUID id,
        UUID ambassadorId,
        UUID ambassadorReferralId,
        String attributionType,
        String eventType,
        String referenceType,
        UUID referenceId,
        String amount,
        String status,
        OffsetDateTime generatedAt) {}
