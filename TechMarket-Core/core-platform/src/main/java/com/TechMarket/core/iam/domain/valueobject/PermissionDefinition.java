package com.techmarket.core.iam.domain.valueobject;

public record PermissionDefinition(Long moduleId, Long resourceId, Long actionId, Long fieldId) {}
