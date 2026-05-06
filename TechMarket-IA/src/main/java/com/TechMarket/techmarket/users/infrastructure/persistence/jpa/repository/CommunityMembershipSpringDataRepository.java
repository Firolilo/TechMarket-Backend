package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityMembershipJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityMembershipSpringDataRepository
        extends JpaRepository<CommunityMembershipJpaEntity, UUID> {

    List<CommunityMembershipJpaEntity> findAllByUserId(UUID userId);

    Optional<CommunityMembershipJpaEntity> findByCommunityIdAndUserId(UUID communityId, UUID userId);
}
