package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientFavoriteJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientFavoriteSpringDataRepository
        extends JpaRepository<ClientFavoriteJpaEntity, UUID> {

    List<ClientFavoriteJpaEntity> findAllByUserIdAndListingIdIsNotNull(UUID userId);

    List<ClientFavoriteJpaEntity> findAllByUserIdAndTenantIdIsNotNullAndListingIdIsNull(
            UUID userId);

    Optional<ClientFavoriteJpaEntity> findByUserIdAndListingId(UUID userId, UUID listingId);

    Optional<ClientFavoriteJpaEntity> findByUserIdAndTenantIdAndListingIdIsNull(
            UUID userId, UUID tenantId);
}
