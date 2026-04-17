package com.techmarket.techmarket.leads.api.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateLeadRequest(
        @NotNull UUID tenantId,
        UUID branchId,
        @NotNull UUID userId,
        @NotNull UUID listingId,
        @NotBlank String sourceChannel,
        @NotBlank String status,
        Integer leadScore,
        String notes) {}
