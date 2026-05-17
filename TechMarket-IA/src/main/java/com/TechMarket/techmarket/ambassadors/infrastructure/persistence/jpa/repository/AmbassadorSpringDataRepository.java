package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorSpringDataRepository extends JpaRepository<AmbassadorJpaEntity, UUID> {

    Optional<AmbassadorJpaEntity> findByUserId(UUID userId);

    List<AmbassadorJpaEntity> findAllBySponsorAmbassadorId(UUID sponsorAmbassadorId);

    long countBySponsorAmbassadorId(UUID sponsorAmbassadorId);
}
