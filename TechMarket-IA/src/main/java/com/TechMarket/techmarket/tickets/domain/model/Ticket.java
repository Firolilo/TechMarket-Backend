package com.techmarket.techmarket.tickets.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Ticket(
        UUID id,
        String ticketCode,
        UUID tenantId,
        UUID customerUserId,
        UUID listingId,
        String subject,
        String priority,
        String status,
        OffsetDateTime openedAt,
        OffsetDateTime createdAt) {}
