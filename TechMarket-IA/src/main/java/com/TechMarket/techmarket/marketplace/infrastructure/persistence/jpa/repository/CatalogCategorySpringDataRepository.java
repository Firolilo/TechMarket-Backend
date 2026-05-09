package com.techmarket.techmarket.marketplace.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.marketplace.infrastructure.persistence.jpa.entity.CatalogCategoryJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogCategorySpringDataRepository
        extends JpaRepository<CatalogCategoryJpaEntity, UUID> {

    List<CatalogCategoryJpaEntity> findAllByParentCategoryIdIsNull();

    List<CatalogCategoryJpaEntity> findAllByParentCategoryId(UUID parentCategoryId);
}
