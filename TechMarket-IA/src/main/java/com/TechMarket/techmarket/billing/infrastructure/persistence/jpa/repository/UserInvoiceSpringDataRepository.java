package com.techmarket.techmarket.billing.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.billing.infrastructure.persistence.jpa.entity.UserInvoiceJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInvoiceSpringDataRepository extends JpaRepository<UserInvoiceJpaEntity, UUID> {

    List<UserInvoiceJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<UserInvoiceJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
