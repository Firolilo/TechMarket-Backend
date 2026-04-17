package com.techmarket.iamservice.infrastructure.persistence.repository;

import com.techmarket.iamservice.infrastructure.persistence.repository.projection.RoleHierarchyView;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleHierarchyRepository
        extends JpaRepository<
                com.techmarket.core.iam.infrastructure.persistence.entity.RoleJpaEntity, Long> {

    @Query(
            value =
                    """
            SELECT r.id AS roleId,
                   r.tenant_id AS tenantId,
                   r.name AS roleName,
                   COALESCE(r.hierarchy_level, 100) AS hierarchyLevel,
                   r.parent_role_id AS parentRoleId
            FROM iam_role r
            WHERE r.id IN (:roleIds)
            """,
            nativeQuery = true)
    List<RoleHierarchyView> findHierarchyByRoleIds(@Param("roleIds") Set<Long> roleIds);

    @Query(
            value =
                    """
            SELECT r.id AS roleId,
                   r.tenant_id AS tenantId,
                   r.name AS roleName,
                   COALESCE(r.hierarchy_level, 100) AS hierarchyLevel,
                   r.parent_role_id AS parentRoleId
            FROM iam_role r
            WHERE LOWER(r.name) IN (:roleNames)
              AND (r.tenant_id = :tenantId OR r.tenant_id = :globalTenantId)
            """,
            nativeQuery = true)
    List<RoleHierarchyView> findHierarchyByTenantAndRoleNames(
            @Param("tenantId") String tenantId,
            @Param("globalTenantId") String globalTenantId,
            @Param("roleNames") Set<String> roleNames);

    @Modifying
    @Query(
            value =
                    """
            UPDATE iam_role
               SET hierarchy_level = :hierarchyLevel,
                   parent_role_id = :parentRoleId
             WHERE id = :roleId
            """,
            nativeQuery = true)
    void updateHierarchy(
            @Param("roleId") Long roleId,
            @Param("hierarchyLevel") Integer hierarchyLevel,
            @Param("parentRoleId") Long parentRoleId);

    @Query(
            value =
                    """
            SELECT r.id
            FROM iam_role r
            WHERE r.tenant_id = :globalTenantId
              AND LOWER(r.name) = LOWER(:roleName)
            LIMIT 1
            """,
            nativeQuery = true)
    Long findGlobalRoleIdByName(
            @Param("globalTenantId") String globalTenantId, @Param("roleName") String roleName);
}
