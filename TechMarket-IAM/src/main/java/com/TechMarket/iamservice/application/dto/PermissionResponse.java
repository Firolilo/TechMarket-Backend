package com.techmarket.iamservice.application.dto;

public record PermissionResponse(
        Long id,
        Long roleId,
        Long moduleId,
        Long resourceId,
        Long actionId,
        Long fieldId,
        String tenantId) {}
