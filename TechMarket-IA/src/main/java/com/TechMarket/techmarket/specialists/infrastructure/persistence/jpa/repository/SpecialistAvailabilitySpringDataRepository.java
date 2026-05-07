package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistAvailabilityJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistAvailabilitySpringDataRepository
        extends JpaRepository<SpecialistAvailabilityJpaEntity, UUID> {

    Optional<SpecialistAvailabilityJpaEntity> findByUserId(UUID userId);
}
