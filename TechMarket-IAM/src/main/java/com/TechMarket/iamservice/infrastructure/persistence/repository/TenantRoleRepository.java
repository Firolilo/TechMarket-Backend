package com.techmarket.iamservice.infrastructure.persistence.repository;

import com.techmarket.core.iam.infrastructure.persistence.entity.RoleJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TenantRoleRepository extends JpaRepository<RoleJpaEntity, Long> {

    Optional<RoleJpaEntity> findByIdAndTenantId(Long id, String tenantId);

    List<RoleJpaEntity> findAllByTenantId(String tenantId);

    Set<RoleJpaEntity> findByIdInAndTenantId(Set<Long> ids, String tenantId);

    Optional<RoleJpaEntity> findByNameIgnoreCaseAndTenantId(String name, String tenantId);

    @Query(
            """
            SELECT r
            FROM RoleJpaEntity r
            WHERE LOWER(r.name) IN :roleNames
              AND (r.tenantId = :tenantId OR r.tenantId = :globalTenantId)
            """)
    List<RoleJpaEntity> findByTenantAndRoleNames(
            @Param("tenantId") String tenantId,
            @Param("globalTenantId") String globalTenantId,
            @Param("roleNames") Set<String> roleNames);
}
