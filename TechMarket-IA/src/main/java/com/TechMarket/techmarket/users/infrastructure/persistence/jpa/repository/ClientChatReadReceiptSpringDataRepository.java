package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatReadReceiptJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientChatReadReceiptSpringDataRepository
        extends JpaRepository<ClientChatReadReceiptJpaEntity, UUID> {

    Optional<ClientChatReadReceiptJpaEntity> findByTicketIdAndUserId(UUID ticketId, UUID userId);
}
