package com.techmarket.iamservice.application.dto;

import java.util.Set;

public record RoleResponse(
        Long id,
        String name,
        String description,
        String tenantId,
        Set<Long> permissionIds,
        Integer hierarchyLevel,
        Long parentRoleId) {}
