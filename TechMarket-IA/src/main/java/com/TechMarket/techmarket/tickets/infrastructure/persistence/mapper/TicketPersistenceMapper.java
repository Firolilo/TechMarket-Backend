package com.techmarket.techmarket.tickets.infrastructure.persistence.mapper;

import com.techmarket.techmarket.tickets.domain.model.Ticket;
import com.techmarket.techmarket.tickets.infrastructure.persistence.jpa.entity.TicketJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketPersistenceMapper {

    public TicketJpaEntity toJpa(Ticket ticket) {
        TicketJpaEntity entity = new TicketJpaEntity();
        entity.setId(ticket.id());
        entity.setTicketCode(ticket.ticketCode());
        entity.setTenantId(ticket.tenantId());
        entity.setCustomerUserId(ticket.customerUserId());
        entity.setListingId(ticket.listingId());
        entity.setSubject(ticket.subject());
        entity.setPriority(ticket.priority());
        entity.setStatus(ticket.status());
        entity.setOpenedAt(ticket.openedAt());
        entity.setCreatedAt(ticket.createdAt());
        return entity;
    }

    public Ticket toDomain(TicketJpaEntity entity) {
        return new Ticket(
                entity.getId(),
                entity.getTicketCode(),
                entity.getTenantId(),
                entity.getCustomerUserId(),
                entity.getListingId(),
                entity.getSubject(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getOpenedAt(),
                entity.getCreatedAt());
    }
}
