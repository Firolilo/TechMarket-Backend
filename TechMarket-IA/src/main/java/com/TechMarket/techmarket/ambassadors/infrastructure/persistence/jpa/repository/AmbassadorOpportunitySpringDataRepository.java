package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorOpportunityJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorOpportunitySpringDataRepository
        extends JpaRepository<AmbassadorOpportunityJpaEntity, UUID> {

    List<AmbassadorOpportunityJpaEntity> findByAmbassadorIdOrderByDetectedAtDesc(UUID ambassadorId);

    Optional<AmbassadorOpportunityJpaEntity> findByIdAndAmbassadorId(UUID id, UUID ambassadorId);
}
