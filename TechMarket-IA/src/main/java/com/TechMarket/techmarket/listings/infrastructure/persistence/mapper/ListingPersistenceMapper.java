package com.techmarket.techmarket.listings.infrastructure.persistence.mapper;

import com.techmarket.techmarket.listings.domain.model.Listing;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ListingPersistenceMapper {

    public ListingJpaEntity toJpa(Listing listing) {
        ListingJpaEntity entity = new ListingJpaEntity();
        entity.setId(listing.id());
        entity.setTenantId(listing.tenantId());
        entity.setCategoryId(listing.categoryId());
        entity.setBrandId(listing.brandId());
        entity.setTitle(listing.title());
        entity.setBasePrice(listing.basePrice());
        entity.setCurrency(listing.currency());
        entity.setStatus(listing.status());
        entity.setCreatedAt(listing.createdAt());
        entity.setUpdatedAt(listing.updatedAt());
        return entity;
    }

    public Listing toDomain(ListingJpaEntity entity) {
        return new Listing(
                entity.getId(),
                entity.getTenantId(),
                entity.getCategoryId(),
                entity.getBrandId(),
                entity.getTitle(),
                entity.getBasePrice(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
