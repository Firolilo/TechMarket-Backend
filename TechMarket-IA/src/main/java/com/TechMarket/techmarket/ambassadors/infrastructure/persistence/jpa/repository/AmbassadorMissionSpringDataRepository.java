package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorMissionJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorMissionSpringDataRepository
        extends JpaRepository<AmbassadorMissionJpaEntity, UUID> {
    List<AmbassadorMissionJpaEntity> findAllByAmbassadorIdOrderByCreatedAtDesc(UUID ambassadorId);

    Optional<AmbassadorMissionJpaEntity> findByIdAndAmbassadorId(UUID id, UUID ambassadorId);
}
