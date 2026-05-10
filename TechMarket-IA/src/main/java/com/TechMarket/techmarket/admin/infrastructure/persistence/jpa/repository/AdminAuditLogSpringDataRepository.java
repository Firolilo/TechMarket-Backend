package com.techmarket.techmarket.admin.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.admin.infrastructure.persistence.jpa.entity.AdminAuditLogJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAuditLogSpringDataRepository
        extends JpaRepository<AdminAuditLogJpaEntity, UUID> {

    List<AdminAuditLogJpaEntity> findTop50ByOrderByCreatedAtDesc();
}
