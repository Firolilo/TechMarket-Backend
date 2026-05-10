package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorPayoutItemJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorPayoutItemSpringDataRepository
        extends JpaRepository<AmbassadorPayoutItemJpaEntity, UUID> {
    List<AmbassadorPayoutItemJpaEntity> findAllByAmbassadorId(UUID ambassadorId);
}
