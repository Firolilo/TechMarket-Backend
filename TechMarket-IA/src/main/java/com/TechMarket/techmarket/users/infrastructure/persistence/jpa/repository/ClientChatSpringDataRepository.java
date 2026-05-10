package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientChatSpringDataRepository extends JpaRepository<ClientChatJpaEntity, UUID> {

    List<ClientChatJpaEntity> findAllByCustomerUserIdAndTicketTypeOrderByCreatedAtDesc(
            UUID customerUserId, String ticketType);

    List<ClientChatJpaEntity> findAllByAssignedTechnicianUserIdAndTicketTypeOrderByCreatedAtDesc(
            UUID assignedTechnicianUserId, String ticketType);

    Optional<ClientChatJpaEntity> findByIdAndCustomerUserId(UUID id, UUID customerUserId);

    Optional<ClientChatJpaEntity> findByIdAndAssignedTechnicianUserId(UUID id, UUID assignedTechnicianUserId);
}
