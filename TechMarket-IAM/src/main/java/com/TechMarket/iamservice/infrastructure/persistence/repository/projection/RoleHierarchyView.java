package com.techmarket.iamservice.infrastructure.persistence.repository.projection;

public interface RoleHierarchyView {
    Long getRoleId();

    String getTenantId();

    String getRoleName();

    Integer getHierarchyLevel();

    Long getParentRoleId();
}
