package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientCartItemJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientCartItemSpringDataRepository
        extends JpaRepository<ClientCartItemJpaEntity, UUID> {

    List<ClientCartItemJpaEntity> findAllByUserId(UUID userId);

    Optional<ClientCartItemJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    Optional<ClientCartItemJpaEntity> findByUserIdAndListingId(UUID userId, UUID listingId);

    void deleteAllByUserId(UUID userId);
}
