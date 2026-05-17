package com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingImageJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingImageSpringDataRepository
        extends JpaRepository<ListingImageJpaEntity, UUID> {

    Optional<ListingImageJpaEntity> findFirstByListingIdAndIsPrimaryTrue(UUID listingId);
}
