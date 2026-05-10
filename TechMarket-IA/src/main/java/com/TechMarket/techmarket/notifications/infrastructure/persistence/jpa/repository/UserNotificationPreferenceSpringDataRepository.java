package com.techmarket.techmarket.notifications.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.notifications.infrastructure.persistence.jpa.entity.UserNotificationPreferenceJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationPreferenceSpringDataRepository
        extends JpaRepository<UserNotificationPreferenceJpaEntity, UUID> {

    Optional<UserNotificationPreferenceJpaEntity> findByUserId(UUID userId);
}
