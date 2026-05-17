package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.PostCommentJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentSpringDataRepository extends JpaRepository<PostCommentJpaEntity, UUID> {

    List<PostCommentJpaEntity> findAllByFeedPostIdOrderByCreatedAtAsc(UUID feedPostId);

    long countByFeedPostId(UUID feedPostId);
}
