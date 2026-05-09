package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistCertificationJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialistCertificationSpringDataRepository
        extends JpaRepository<SpecialistCertificationJpaEntity, UUID> {

    List<SpecialistCertificationJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<SpecialistCertificationJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
