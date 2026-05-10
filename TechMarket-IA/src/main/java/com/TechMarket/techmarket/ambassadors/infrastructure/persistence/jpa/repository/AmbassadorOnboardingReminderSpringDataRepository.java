package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorOnboardingReminderJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorOnboardingReminderSpringDataRepository
        extends JpaRepository<AmbassadorOnboardingReminderJpaEntity, UUID> {}
