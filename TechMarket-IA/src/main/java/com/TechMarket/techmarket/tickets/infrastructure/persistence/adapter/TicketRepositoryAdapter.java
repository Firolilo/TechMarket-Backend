package com.techmarket.techmarket.tickets.infrastructure.persistence.adapter;

import com.techmarket.techmarket.tickets.domain.model.Ticket;
import com.techmarket.techmarket.tickets.domain.port.TicketRepositoryPort;
import com.techmarket.techmarket.tickets.infrastructure.persistence.jpa.repository.TicketSpringDataRepository;
import com.techmarket.techmarket.tickets.infrastructure.persistence.mapper.TicketPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TicketRepositoryAdapter implements TicketRepositoryPort {

    private final TicketSpringDataRepository springDataRepository;
    private final TicketPersistenceMapper mapper;

    public TicketRepositoryAdapter(
            TicketSpringDataRepository springDataRepository, TicketPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Ticket save(Ticket ticket) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(ticket)));
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
}
