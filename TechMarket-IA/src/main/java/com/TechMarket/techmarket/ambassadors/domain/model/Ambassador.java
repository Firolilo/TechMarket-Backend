package com.techmarket.techmarket.ambassadors.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Ambassador(
        UUID id,
        UUID userId,
        String referralCode,
        String status,
        String level,
        OffsetDateTime activatedAt) {}
