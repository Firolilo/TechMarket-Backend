package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorOnboardingMilestoneJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorOnboardingMilestoneSpringDataRepository
        extends JpaRepository<AmbassadorOnboardingMilestoneJpaEntity, UUID> {

    Optional<AmbassadorOnboardingMilestoneJpaEntity> findByAmbassadorReferralIdAndMilestoneCode(
            UUID referralId, String code);
}
