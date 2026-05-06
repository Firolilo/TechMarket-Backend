package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientAddressJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAddressSpringDataRepository
        extends JpaRepository<ClientAddressJpaEntity, UUID> {

    List<ClientAddressJpaEntity> findAllByUserIdOrderByDefaultAddressDescCreatedAtAsc(UUID userId);

    Optional<ClientAddressJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    List<ClientAddressJpaEntity> findAllByUserId(UUID userId);
}
