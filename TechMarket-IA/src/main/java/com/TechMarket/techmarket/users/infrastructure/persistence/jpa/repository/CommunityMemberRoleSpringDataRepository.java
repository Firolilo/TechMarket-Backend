package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityMemberRoleJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityMemberRoleSpringDataRepository
        extends JpaRepository<CommunityMemberRoleJpaEntity, UUID> {

    List<CommunityMemberRoleJpaEntity> findAllByCommunityId(UUID communityId);

    Optional<CommunityMemberRoleJpaEntity> findByCommunityIdAndUserId(
            UUID communityId, UUID userId);

    long countByCommunityIdAndRole(UUID communityId, String role);
}
