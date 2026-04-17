package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;

public record AssignPermissionsToRoleRequest(
        @NotNull(message = "{validation.role.id.required}") Long roleId,
        @NotNull(message = "tenantId is required") String tenantId,
        @NotEmpty(message = "{validation.permission.ids.required}")
                Collection<@NotNull Long> permissionIds) {}
