package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorReferralSpringDataRepository
        extends JpaRepository<AmbassadorReferralJpaEntity, UUID> {

    List<AmbassadorReferralJpaEntity> findByAmbassadorId(UUID ambassadorId);
}
