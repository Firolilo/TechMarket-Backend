package com.techmarket.techmarket.tickets.domain.port;

import com.techmarket.techmarket.tickets.domain.model.Ticket;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepositoryPort {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(UUID id);
}
