package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorWithdrawalJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorWithdrawalSpringDataRepository
        extends JpaRepository<AmbassadorWithdrawalJpaEntity, UUID> {
    List<AmbassadorWithdrawalJpaEntity> findAllByAmbassadorIdOrderByRequestedAtDesc(
            UUID ambassadorId);
}
