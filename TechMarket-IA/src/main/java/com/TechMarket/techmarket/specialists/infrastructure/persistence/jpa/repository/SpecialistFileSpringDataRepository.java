package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistFileJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistFileSpringDataRepository
        extends JpaRepository<SpecialistFileJpaEntity, UUID> {

    List<SpecialistFileJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<SpecialistFileJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
