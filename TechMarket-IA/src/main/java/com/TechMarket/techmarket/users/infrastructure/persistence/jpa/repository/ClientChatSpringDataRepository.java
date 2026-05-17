package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientChatSpringDataRepository extends JpaRepository<ClientChatJpaEntity, UUID> {

    List<ClientChatJpaEntity> findAllByCustomerUserIdAndTicketTypeOrderByCreatedAtDesc(
            UUID customerUserId, String ticketType);

    List<ClientChatJpaEntity> findAllByAssignedTechnicianUserIdAndTicketTypeOrderByCreatedAtDesc(
            UUID assignedTechnicianUserId, String ticketType);

    @Query(
            """
            select chat
            from ClientChatJpaEntity chat
            where chat.ticketType = :ticketType
              and (chat.customerUserId = :userId or chat.assignedTechnicianUserId = :userId)
            order by chat.createdAt desc
            """)
    List<ClientChatJpaEntity> findAllUserConversations(UUID userId, String ticketType);

    Optional<ClientChatJpaEntity> findByIdAndCustomerUserId(UUID id, UUID customerUserId);

    Optional<ClientChatJpaEntity> findByIdAndCustomerUserIdAndTicketType(
            UUID id, UUID customerUserId, String ticketType);

    List<ClientChatJpaEntity> findAllByTenantId(UUID tenantId);

    Optional<ClientChatJpaEntity> findByIdAndAssignedTechnicianUserId(
            UUID id, UUID assignedTechnicianUserId);

    @Query(
            """
            select chat
            from ClientChatJpaEntity chat
            where chat.id = :id
              and (chat.customerUserId = :userId or chat.assignedTechnicianUserId = :userId)
            """)
    Optional<ClientChatJpaEntity> findUserConversation(UUID id, UUID userId);
}
