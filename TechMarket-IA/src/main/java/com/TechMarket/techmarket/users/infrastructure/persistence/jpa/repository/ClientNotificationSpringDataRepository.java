package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientNotificationJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientNotificationSpringDataRepository
        extends JpaRepository<ClientNotificationJpaEntity, UUID> {

    List<ClientNotificationJpaEntity> findAllByUserIdOrderBySentAtDesc(UUID userId);

    List<ClientNotificationJpaEntity> findAllByUserIdAndReadFalse(UUID userId);

    long countByUserIdAndReadFalse(UUID userId);

    Optional<ClientNotificationJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
