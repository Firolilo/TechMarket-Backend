package com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository;

import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TenantSpringDataRepository extends JpaRepository<TenantJpaEntity, UUID> {

    @Query(
            value =
                    """
            SELECT t.* FROM tenants t
            INNER JOIN tenant_members tm ON t.id = tm.tenant_id
            WHERE tm.user_id = :userId
              AND tm.status = 'ACTIVE'
            LIMIT 1
            """,
            nativeQuery = true)
    Optional<TenantJpaEntity> findFirstByMemberUserId(@Param("userId") UUID userId);
}
