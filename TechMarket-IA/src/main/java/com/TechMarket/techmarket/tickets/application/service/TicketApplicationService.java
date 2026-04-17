package com.techmarket.techmarket.tickets.application.service;

import com.techmarket.techmarket.tickets.application.command.CreateTicketCommand;
import com.techmarket.techmarket.tickets.application.query.GetTicketByIdQuery;
import com.techmarket.techmarket.tickets.domain.model.Ticket;
import com.techmarket.techmarket.tickets.domain.port.TicketRepositoryPort;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketApplicationService {

    private final TicketRepositoryPort repository;

    public TicketApplicationService(TicketRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public Ticket create(CreateTicketCommand command) {
        OffsetDateTime now = OffsetDateTime.now();
        Ticket ticket =
                new Ticket(
                        UUID.randomUUID(),
                        command.ticketCode(),
                        command.tenantId(),
                        command.customerUserId(),
                        command.listingId(),
                        command.subject(),
                        command.priority(),
                        command.status(),
                        now,
                        now);
        return repository.save(ticket);
    }

    @Transactional(readOnly = true)
    public Ticket getById(GetTicketByIdQuery query) {
        return repository.findById(query.id()).orElse(null);
    }
}
