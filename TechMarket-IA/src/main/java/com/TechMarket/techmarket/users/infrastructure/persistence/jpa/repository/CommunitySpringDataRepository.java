package com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.CommunityJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunitySpringDataRepository extends JpaRepository<CommunityJpaEntity, UUID> {}
