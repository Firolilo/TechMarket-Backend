package com.techmarket.techmarket.payments.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.entity.UserTransactionJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTransactionSpringDataRepository
        extends JpaRepository<UserTransactionJpaEntity, UUID> {

    List<UserTransactionJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByCreatedAtAfter(java.time.OffsetDateTime createdAt);

    Optional<UserTransactionJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
