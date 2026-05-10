package com.techmarket.techmarket.billing.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.billing.infrastructure.persistence.jpa.entity.UserPaymentRefundJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPaymentRefundSpringDataRepository
        extends JpaRepository<UserPaymentRefundJpaEntity, UUID> {}
