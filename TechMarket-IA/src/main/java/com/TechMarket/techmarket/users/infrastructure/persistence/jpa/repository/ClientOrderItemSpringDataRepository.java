package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientOrderItemJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientOrderItemSpringDataRepository
        extends JpaRepository<ClientOrderItemJpaEntity, UUID> {

    List<ClientOrderItemJpaEntity> findAllByOrderId(UUID orderId);
}
