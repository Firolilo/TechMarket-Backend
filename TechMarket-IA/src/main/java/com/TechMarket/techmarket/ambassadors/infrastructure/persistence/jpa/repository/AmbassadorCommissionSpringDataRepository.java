package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorCommissionJpaEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorCommissionSpringDataRepository
        extends JpaRepository<AmbassadorCommissionJpaEntity, UUID> {

    List<AmbassadorCommissionJpaEntity> findByAmbassadorId(UUID ambassadorId);

    List<AmbassadorCommissionJpaEntity> findAllByAmbassadorIdOrderByGeneratedAtDesc(UUID ambassadorId);
}
