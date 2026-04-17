package com.techmarket.techmarket.tickets.api.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateTicketRequest(
        @NotBlank String ticketCode,
        @NotNull UUID tenantId,
        @NotNull UUID customerUserId,
        @NotNull UUID listingId,
        @NotBlank String subject,
        @NotBlank String priority,
        @NotBlank String status) {}
