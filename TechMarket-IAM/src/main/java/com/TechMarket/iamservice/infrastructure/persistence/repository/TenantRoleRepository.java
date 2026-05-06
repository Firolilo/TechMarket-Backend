package com.techmarket.iamservice.infrastructure.persistence.repository;

import com.techmarket.iamservice.infrastructure.persistence.entity.RoleEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TenantRoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByIdAndTenantId(Long id, String tenantId);

    List<RoleEntity> findAllByTenantId(String tenantId);

    Set<RoleEntity> findByIdInAndTenantId(Set<Long> ids, String tenantId);

    Optional<RoleEntity> findByNameIgnoreCaseAndTenantId(String name, String tenantId);

    @Query(
            """
            SELECT r
            FROM RoleEntity r
            WHERE LOWER(r.name) IN :roleNames
              AND (r.tenantId = :tenantId OR r.tenantId = :globalTenantId)
            """)
    List<RoleEntity> findByTenantAndRoleNames(
            @Param("tenantId") String tenantId,
            @Param("globalTenantId") String globalTenantId,
            @Param("roleNames") Set<String> roleNames);
}
