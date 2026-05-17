package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityPostLikeJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostLikeSpringDataRepository
        extends JpaRepository<CommunityPostLikeJpaEntity, UUID> {

    List<CommunityPostLikeJpaEntity> findAllByPostId(UUID postId);

    Optional<CommunityPostLikeJpaEntity> findByPostIdAndUserId(UUID postId, UUID userId);

    long countByPostId(UUID postId);
}
