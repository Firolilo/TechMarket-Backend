package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorLeadActivityJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorLeadActivitySpringDataRepository
        extends JpaRepository<AmbassadorLeadActivityJpaEntity, UUID> {

    List<AmbassadorLeadActivityJpaEntity> findAllByLeadIdOrderByCreatedAtDesc(UUID leadId);
}
