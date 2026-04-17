package com.techmarket.iamservice.application.service;

import com.techmarket.core.iam.infrastructure.persistence.entity.PermissionJpaEntity;
import com.techmarket.iamservice.application.dto.PermissionResponse;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantPermissionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionQueryService {

    private final TenantPermissionRepository tenantPermissionRepository;

    public PermissionQueryService(TenantPermissionRepository tenantPermissionRepository) {
        this.tenantPermissionRepository = tenantPermissionRepository;
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return tenantPermissionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> findAllByTenant(String tenantId) {
        return tenantPermissionRepository.findAllByTenantId(tenantId).stream()
                .map(this::toResponse)
                .toList();
    }

    private PermissionResponse toResponse(PermissionJpaEntity permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getRole() != null ? permission.getRole().getId() : null,
                permission.getModule() != null ? permission.getModule().getId() : null,
                permission.getResource() != null ? permission.getResource().getId() : null,
                permission.getAction() != null ? permission.getAction().getId() : null,
                permission.getField() != null ? permission.getField().getId() : null,
                permission.getTenantId());
    }
}
