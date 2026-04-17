package com.techmarket.core.iam.infrastructure.persistence.mapper;

import com.techmarket.core.iam.domain.model.Permission;
import com.techmarket.core.iam.infrastructure.persistence.entity.PermissionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PermissionJpaMapper {
    public Permission toDomain(PermissionJpaEntity entity) {
        if (entity == null) return null;
        return new Permission(
                entity.getId(),
                entity.getRole() != null ? entity.getRole().getId() : null,
                entity.getModule() != null ? entity.getModule().getId() : null,
                entity.getResource() != null ? entity.getResource().getId() : null,
                entity.getAction() != null ? entity.getAction().getId() : null,
                entity.getField() != null ? entity.getField().getId() : null,
                entity.getTenantId(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getLastModifiedAt(),
                entity.getLastModifiedBy());
    }
}
