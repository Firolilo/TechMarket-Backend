package com.techmarket.techmarket.ambassadors.api.mobile.response;

import java.util.UUID;

public record OpportunityResponse(
        UUID id,
        String type,
        String zone,
        String description,
        String potential,
        String dataSource,
        String status,
        boolean isSaved) {}
