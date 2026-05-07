package com.techmarket.techmarket.search.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.search.infrastructure.persistence.jpa.entity.SearchHistoryJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistorySpringDataRepository extends JpaRepository<SearchHistoryJpaEntity, UUID> {

    List<SearchHistoryJpaEntity> findAllByUserIdOrderBySearchedAtDesc(UUID userId);

    Optional<SearchHistoryJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
