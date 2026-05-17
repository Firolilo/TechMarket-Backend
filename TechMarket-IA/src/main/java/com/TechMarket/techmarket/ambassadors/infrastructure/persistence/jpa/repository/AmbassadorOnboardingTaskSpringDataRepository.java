package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorOnboardingTaskJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorOnboardingTaskSpringDataRepository
        extends JpaRepository<AmbassadorOnboardingTaskJpaEntity, UUID> {

    List<AmbassadorOnboardingTaskJpaEntity> findAllByAmbassadorReferralIdOrderByCreatedAtDesc(
            UUID referralId);

    Optional<AmbassadorOnboardingTaskJpaEntity> findByIdAndAmbassadorReferralId(
            UUID id, UUID referralId);
}
