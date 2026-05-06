package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatMessageJpaEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientChatMessageSpringDataRepository
        extends JpaRepository<ClientChatMessageJpaEntity, UUID> {

    List<ClientChatMessageJpaEntity> findAllByTicketIdOrderByCreatedAtAsc(UUID ticketId);

    Optional<ClientChatMessageJpaEntity> findTopByTicketIdOrderByCreatedAtDesc(UUID ticketId);

    long countByTicketIdAndAuthorUserIdNot(UUID ticketId, UUID authorUserId);

    long countByTicketIdAndAuthorUserIdNotAndCreatedAtAfter(
            UUID ticketId, UUID authorUserId, OffsetDateTime createdAt);
}
