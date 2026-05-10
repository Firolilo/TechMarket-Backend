package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorLeadJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorLeadSpringDataRepository
        extends JpaRepository<AmbassadorLeadJpaEntity, UUID> {

    List<AmbassadorLeadJpaEntity> findAllByAmbassadorIdOrderByCreatedAtDesc(UUID ambassadorId);

    Optional<AmbassadorLeadJpaEntity> findByIdAndAmbassadorId(UUID id, UUID ambassadorId);
}
