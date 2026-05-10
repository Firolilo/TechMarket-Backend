package com.techmarket.techmarket.payments.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.entity.UserPaymentIntentJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPaymentIntentSpringDataRepository
        extends JpaRepository<UserPaymentIntentJpaEntity, UUID> {

    Optional<UserPaymentIntentJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
