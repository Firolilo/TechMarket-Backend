package com.techmarket.techmarket.ambassadors.api.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AmbassadorResponse(
        UUID id,
        UUID userId,
        String referralCode,
        String status,
        String level,
        OffsetDateTime activatedAt) {}
