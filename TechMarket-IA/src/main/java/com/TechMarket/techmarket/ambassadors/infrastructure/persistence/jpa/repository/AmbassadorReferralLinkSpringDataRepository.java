package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralLinkJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorReferralLinkSpringDataRepository
        extends JpaRepository<AmbassadorReferralLinkJpaEntity, UUID> {

    List<AmbassadorReferralLinkJpaEntity> findAllByAmbassadorIdOrderByCreatedAtDesc(UUID ambassadorId);

    List<AmbassadorReferralLinkJpaEntity> findAllByAmbassadorIdAndActiveTrueOrderByCreatedAtDesc(UUID ambassadorId);

    Optional<AmbassadorReferralLinkJpaEntity> findByIdAndAmbassadorId(UUID id, UUID ambassadorId);

    Optional<AmbassadorReferralLinkJpaEntity> findByCode(String code);
}
