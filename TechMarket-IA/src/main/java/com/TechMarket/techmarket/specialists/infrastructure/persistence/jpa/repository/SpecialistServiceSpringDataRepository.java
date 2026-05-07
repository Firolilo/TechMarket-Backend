package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistServiceSpringDataRepository
        extends JpaRepository<SpecialistServiceJpaEntity, UUID> {

    List<SpecialistServiceJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<SpecialistServiceJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
