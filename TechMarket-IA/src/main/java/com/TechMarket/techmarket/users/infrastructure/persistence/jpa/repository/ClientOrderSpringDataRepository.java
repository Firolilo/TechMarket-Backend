package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientOrderJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientOrderSpringDataRepository extends JpaRepository<ClientOrderJpaEntity, UUID> {

    List<ClientOrderJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<ClientOrderJpaEntity> findByIdAndUserId(UUID id, UUID userId);
}
