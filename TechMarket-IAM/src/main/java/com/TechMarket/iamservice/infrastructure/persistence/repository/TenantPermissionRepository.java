package com.techmarket.iamservice.infrastructure.persistence.repository;

import com.techmarket.iamservice.infrastructure.persistence.entity.PermissionEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantPermissionRepository extends JpaRepository<PermissionEntity, Long> {
    List<PermissionEntity> findAllByTenantId(String tenantId);

    Set<PermissionEntity> findByIdInAndTenantId(Set<Long> ids, String tenantId);
}
