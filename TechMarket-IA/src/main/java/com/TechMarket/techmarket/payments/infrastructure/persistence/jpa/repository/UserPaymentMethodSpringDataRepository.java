package com.techmarket.techmarket.payments.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.payments.infrastructure.persistence.jpa.entity.UserPaymentMethodJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPaymentMethodSpringDataRepository
        extends JpaRepository<UserPaymentMethodJpaEntity, UUID> {

    List<UserPaymentMethodJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<UserPaymentMethodJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
