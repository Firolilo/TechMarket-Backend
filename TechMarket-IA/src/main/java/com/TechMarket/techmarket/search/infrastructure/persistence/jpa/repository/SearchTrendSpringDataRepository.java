package com.techmarket.techmarket.search.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.search.infrastructure.persistence.jpa.entity.SearchTrendJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchTrendSpringDataRepository extends JpaRepository<SearchTrendJpaEntity, UUID> {

    List<SearchTrendJpaEntity> findTop10ByOrderBySearchCountDescUpdatedAtDesc();

    Optional<SearchTrendJpaEntity> findByQueryTextIgnoreCase(String queryText);
}
