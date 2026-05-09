package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistProfileJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistProfileSpringDataRepository
        extends JpaRepository<SpecialistProfileJpaEntity, UUID> {

    Optional<SpecialistProfileJpaEntity> findByUserId(UUID userId);
}
