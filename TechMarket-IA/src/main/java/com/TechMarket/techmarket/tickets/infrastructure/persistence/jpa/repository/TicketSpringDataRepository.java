package com.techmarket.techmarket.tickets.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.tickets.infrastructure.persistence.jpa.entity.TicketJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketSpringDataRepository extends JpaRepository<TicketJpaEntity, UUID> {}
