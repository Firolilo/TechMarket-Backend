package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorPayoutMethodJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorPayoutMethodSpringDataRepository
        extends JpaRepository<AmbassadorPayoutMethodJpaEntity, UUID> {

    List<AmbassadorPayoutMethodJpaEntity> findAllByAmbassadorIdOrderByCreatedAtDesc(UUID ambassadorId);

    Optional<AmbassadorPayoutMethodJpaEntity> findByIdAndAmbassadorId(UUID id, UUID ambassadorId);

    boolean existsByAmbassadorId(UUID ambassadorId);
}
