package com.techmarket.techmarket.admin.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.admin.infrastructure.persistence.jpa.entity.AdminModerationActionJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminModerationActionSpringDataRepository
        extends JpaRepository<AdminModerationActionJpaEntity, UUID> {}
