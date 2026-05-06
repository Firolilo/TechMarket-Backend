package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientReviewJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientReviewSpringDataRepository
        extends JpaRepository<ClientReviewJpaEntity, UUID> {

    List<ClientReviewJpaEntity> findAllByListingIdOrderByCreatedAtDesc(UUID listingId);

    Optional<ClientReviewJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
