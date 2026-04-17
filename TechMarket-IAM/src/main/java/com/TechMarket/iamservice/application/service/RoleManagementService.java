package com.techmarket.iamservice.application.service;

import com.techmarket.core.iam.infrastructure.persistence.entity.PermissionJpaEntity;
import com.techmarket.core.iam.infrastructure.persistence.entity.RoleJpaEntity;
import com.techmarket.core.shared.exceptions.EntityNotFoundException;
import com.techmarket.iamservice.application.dto.CreateRoleRequest;
import com.techmarket.iamservice.application.dto.RoleResponse;
import com.techmarket.iamservice.application.model.IamConstants;
import com.techmarket.iamservice.infrastructure.persistence.repository.RoleHierarchyRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantRoleRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.projection.RoleHierarchyView;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleManagementService {

    private final TenantRoleRepository tenantRoleRepository;
    private final RoleHierarchyRepository roleHierarchyRepository;
    private final AuditTrailService auditTrailService;
    private final PrincipalAccessService principalAccessService;

    public RoleManagementService(
            TenantRoleRepository tenantRoleRepository,
            RoleHierarchyRepository roleHierarchyRepository,
            AuditTrailService auditTrailService,
            PrincipalAccessService principalAccessService) {
        this.tenantRoleRepository = tenantRoleRepository;
        this.roleHierarchyRepository = roleHierarchyRepository;
        this.auditTrailService = auditTrailService;
        this.principalAccessService = principalAccessService;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        List<RoleJpaEntity> roles = tenantRoleRepository.findAll();
        Map<Long, RoleHierarchyView> hierarchyByRoleId =
                roleHierarchyRepository
                        .findHierarchyByRoleIds(
                                roles.stream()
                                        .map(RoleJpaEntity::getId)
                                        .collect(Collectors.toSet()))
                        .stream()
                        .collect(Collectors.toMap(RoleHierarchyView::getRoleId, item -> item));

        return roles.stream()
                .map(role -> toResponse(role, hierarchyByRoleId.get(role.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findAllByTenant(String tenantId) {
        List<RoleJpaEntity> roles = tenantRoleRepository.findAllByTenantId(tenantId);
        Map<Long, RoleHierarchyView> hierarchyByRoleId =
                roleHierarchyRepository
                        .findHierarchyByRoleIds(
                                roles.stream()
                                        .map(RoleJpaEntity::getId)
                                        .collect(Collectors.toSet()))
                        .stream()
                        .collect(Collectors.toMap(RoleHierarchyView::getRoleId, item -> item));

        return roles.stream()
                .map(role -> toResponse(role, hierarchyByRoleId.get(role.getId())))
                .toList();
    }

    @Transactional
    public RoleResponse create(
            String tenantId, CreateRoleRequest request, Authentication authentication) {
        RoleJpaEntity role = new RoleJpaEntity(request.name().trim(), request.description());
        role.setTenantId(tenantId);
        RoleJpaEntity saved = tenantRoleRepository.save(role);

        Integer hierarchyLevel =
                resolveHierarchyLevel(tenantId, request.hierarchyLevel(), request.parentRoleId());
        roleHierarchyRepository.updateHierarchy(
                saved.getId(), hierarchyLevel, request.parentRoleId());

        RoleHierarchyView hierarchy =
                roleHierarchyRepository.findHierarchyByRoleIds(Set.of(saved.getId())).stream()
                        .findFirst()
                        .orElse(null);

        Long actorUserId = principalAccessService.currentUserId(authentication);
        auditTrailService.record(
                "ROLE_CREATED",
                "Role",
                saved.getId().toString(),
                tenantId,
                actorUserId == null ? "system" : actorUserId.toString());
        return toResponse(saved, hierarchy);
    }

    private Integer resolveHierarchyLevel(
            String tenantId, Integer requestedHierarchyLevel, Long parentRoleId) {
        if (requestedHierarchyLevel != null) {
            if (requestedHierarchyLevel < 0) {
                throw new IllegalArgumentException("hierarchyLevel must be non-negative");
            }
            if (parentRoleId != null) {
                RoleHierarchyView parent =
                        roleHierarchyRepository
                                .findHierarchyByRoleIds(Set.of(parentRoleId))
                                .stream()
                                .findFirst()
                                .orElseThrow(
                                        () ->
                                                new EntityNotFoundException(
                                                        "Role", parentRoleId.toString()));
                if (!tenantId.equals(parent.getTenantId())) {
                    throw new IllegalArgumentException("parentRoleId must belong to same tenant");
                }
                if (requestedHierarchyLevel <= parent.getHierarchyLevel()) {
                    throw new IllegalArgumentException(
                            "hierarchyLevel must be greater than parent role hierarchy");
                }
            }
            return requestedHierarchyLevel;
        }

        if (parentRoleId == null) {
            return IamConstants.DEFAULT_HIERARCHY_LEVEL;
        }

        RoleHierarchyView parent =
                roleHierarchyRepository.findHierarchyByRoleIds(Set.of(parentRoleId)).stream()
                        .findFirst()
                        .orElseThrow(
                                () -> new EntityNotFoundException("Role", parentRoleId.toString()));
        if (!tenantId.equals(parent.getTenantId())) {
            throw new IllegalArgumentException("parentRoleId must belong to same tenant");
        }
        return Math.max(IamConstants.DEFAULT_HIERARCHY_LEVEL, parent.getHierarchyLevel() + 1);
    }

    private RoleResponse toResponse(RoleJpaEntity role, RoleHierarchyView hierarchy) {
        Set<Long> permissionIds =
                role.getPermissions().stream()
                        .map(PermissionJpaEntity::getId)
                        .collect(Collectors.toSet());

        Integer hierarchyLevel =
                hierarchy == null
                        ? IamConstants.DEFAULT_HIERARCHY_LEVEL
                        : hierarchy.getHierarchyLevel();
        Long parentRoleId = hierarchy == null ? null : hierarchy.getParentRoleId();

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getTenantId(),
                permissionIds,
                hierarchyLevel,
                parentRoleId);
    }
}
