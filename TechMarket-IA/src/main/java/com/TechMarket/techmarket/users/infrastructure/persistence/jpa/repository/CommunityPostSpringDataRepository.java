package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityPostJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostSpringDataRepository
        extends JpaRepository<CommunityPostJpaEntity, UUID> {

    List<CommunityPostJpaEntity> findAllByCommunityIdOrderByCreatedAtDesc(UUID communityId);
}
