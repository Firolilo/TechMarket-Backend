package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralFileJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorReferralFileSpringDataRepository
        extends JpaRepository<AmbassadorReferralFileJpaEntity, UUID> {}
