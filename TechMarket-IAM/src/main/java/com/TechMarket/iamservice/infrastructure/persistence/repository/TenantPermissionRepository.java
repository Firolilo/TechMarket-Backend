package com.techmarket.iamservice.infrastructure.persistence.repository;

import com.techmarket.core.iam.infrastructure.persistence.entity.PermissionJpaEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantPermissionRepository extends JpaRepository<PermissionJpaEntity, Long> {
    List<PermissionJpaEntity> findAllByTenantId(String tenantId);

    Set<PermissionJpaEntity> findByIdInAndTenantId(Set<Long> ids, String tenantId);
}
