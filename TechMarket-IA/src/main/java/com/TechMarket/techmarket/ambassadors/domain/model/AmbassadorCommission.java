package com.techmarket.techmarket.ambassadors.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AmbassadorCommission(
        UUID id,
        UUID ambassadorId,
        UUID ambassadorReferralId,
        UUID commissionRuleId,
        String attributionType,
        String eventType,
        String referenceType,
        UUID referenceId,
        String amount,
        String status,
        OffsetDateTime generatedAt) {}
