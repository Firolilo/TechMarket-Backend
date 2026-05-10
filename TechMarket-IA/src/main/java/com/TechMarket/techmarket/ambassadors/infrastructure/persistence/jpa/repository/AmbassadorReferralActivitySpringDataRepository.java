package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralActivityJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorReferralActivitySpringDataRepository
        extends JpaRepository<AmbassadorReferralActivityJpaEntity, UUID> {

    List<AmbassadorReferralActivityJpaEntity> findAllByAmbassadorReferralIdOrderByCreatedAtDesc(UUID referralId);
}
