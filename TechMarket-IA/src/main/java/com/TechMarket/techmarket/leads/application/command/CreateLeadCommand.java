package com.techmarket.techmarket.leads.application.command;

import java.util.UUID;

public record CreateLeadCommand(
        UUID tenantId,
        UUID branchId,
        UUID userId,
        UUID listingId,
        String sourceChannel,
        String status,
        Integer leadScore,
        String notes) {}
