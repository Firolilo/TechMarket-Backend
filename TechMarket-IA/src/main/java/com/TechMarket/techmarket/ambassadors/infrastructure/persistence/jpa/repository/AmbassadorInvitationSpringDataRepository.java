package com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorInvitationJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbassadorInvitationSpringDataRepository
        extends JpaRepository<AmbassadorInvitationJpaEntity, UUID> {

    List<AmbassadorInvitationJpaEntity> findAllByAmbassadorIdOrderByCreatedAtDesc(UUID ambassadorId);

    Optional<AmbassadorInvitationJpaEntity> findByIdAndAmbassadorId(UUID id, UUID ambassadorId);
}
