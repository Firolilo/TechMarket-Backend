package com.techmarket.techmarket.tickets.application.command;

import java.util.UUID;

public record CreateTicketCommand(
        String ticketCode,
        UUID tenantId,
        UUID customerUserId,
        UUID listingId,
        String subject,
        String priority,
        String status) {}
