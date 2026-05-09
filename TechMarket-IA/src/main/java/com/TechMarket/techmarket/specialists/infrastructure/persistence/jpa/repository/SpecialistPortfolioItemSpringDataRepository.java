package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistPortfolioItemJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistPortfolioItemSpringDataRepository
        extends JpaRepository<SpecialistPortfolioItemJpaEntity, UUID> {

    List<SpecialistPortfolioItemJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<SpecialistPortfolioItemJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
