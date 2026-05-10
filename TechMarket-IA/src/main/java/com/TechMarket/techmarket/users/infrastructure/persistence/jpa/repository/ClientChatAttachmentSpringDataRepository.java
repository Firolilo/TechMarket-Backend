package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatAttachmentJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientChatAttachmentSpringDataRepository
        extends JpaRepository<ClientChatAttachmentJpaEntity, UUID> {

    List<ClientChatAttachmentJpaEntity> findAllByTicketIdOrderByCreatedAtDesc(UUID ticketId);
}
