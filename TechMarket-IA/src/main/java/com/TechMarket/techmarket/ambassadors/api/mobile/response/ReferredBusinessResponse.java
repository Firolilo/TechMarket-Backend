package com.techmarket.techmarket.ambassadors.api.mobile.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReferredBusinessResponse(
        UUID id,
        String name,
        String zone,
        String type,
        String status,
        double monthlyImpact,
        double monthlyIncome,
        double totalImpact,
        double totalIncome,
        int referralLevel,
        OffsetDateTime referralDate,
        OffsetDateTime lastActivityDate) {}
