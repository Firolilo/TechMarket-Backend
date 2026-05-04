package com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingSpringDataRepository extends JpaRepository<ListingJpaEntity, UUID> {

    List<ListingJpaEntity> findAllByCategoryId(UUID categoryId);

    List<ListingJpaEntity> findAllByTenantId(UUID tenantId);
}
