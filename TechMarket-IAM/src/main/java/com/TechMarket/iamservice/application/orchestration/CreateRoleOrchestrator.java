package com.techmarket.iamservice.application.orchestration;

import com.techmarket.core.iam.application.command.CreateRoleCommand;
import com.techmarket.core.iam.application.usecase.CreateRoleUseCase;
import com.techmarket.core.iam.domain.model.Role;
import com.techmarket.core.shared.exceptions.EntityNotFoundException;
import com.techmarket.core.shared.exceptions.TechMarketException;
import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.application.dto.CreateRoleRequest;
import com.techmarket.iamservice.application.dto.RoleResponse;
import com.techmarket.iamservice.application.exception.IamServiceException;
import com.techmarket.iamservice.application.model.IamConstants;
import com.techmarket.iamservice.application.service.AuditTrailService;
import com.techmarket.iamservice.application.service.PrincipalAccessService;
import com.techmarket.iamservice.infrastructure.persistence.repository.RoleHierarchyRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantRoleRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.projection.RoleHierarchyView;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

public class CreateRoleOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(CreateRoleOrchestrator.class);

    private final CreateRoleUseCase createRoleUseCase;
    private final TenantRoleRepository tenantRoleRepository;
    private final RoleHierarchyRepository roleHierarchyRepository;
    private final AuditTrailService auditTrailService;
    private final PrincipalAccessService principalAccessService;

    public CreateRoleOrchestrator(
            CreateRoleUseCase createRoleUseCase,
            TenantRoleRepository tenantRoleRepository,
            RoleHierarchyRepository roleHierarchyRepository,
            AuditTrailService auditTrailService,
            PrincipalAccessService principalAccessService) {
        this.createRoleUseCase = createRoleUseCase;
        this.tenantRoleRepository = tenantRoleRepository;
        this.roleHierarchyRepository = roleHierarchyRepository;
        this.auditTrailService = auditTrailService;
        this.principalAccessService = principalAccessService;
    }

    public RoleResponse execute(
            String tenantId, CreateRoleRequest request, Authentication authentication) {
        log.info(
                "event=IAM_ROLE_CREATE_REQUEST_RECEIVED tenantId={} name={}",
                tenantId,
                request.name());

        try {
            CreateRoleCommand command =
                    new CreateRoleCommand(request.name().trim(), request.description());
            Role role = createRoleUseCase.execute(command);

            Integer hierarchyLevel =
                    resolveHierarchyLevel(
                            tenantId, request.hierarchyLevel(), request.parentRoleId());
            roleHierarchyRepository.updateHierarchy(
                    role.getId(), hierarchyLevel, request.parentRoleId());

            RoleHierarchyView hierarchy =
                    roleHierarchyRepository.findHierarchyByRoleIds(Set.of(role.getId())).stream()
                            .findFirst()
                            .orElse(null);

            Long actorUserId = principalAccessService.currentUserId(authentication);
            auditTrailService.record(
                    "ROLE_CREATED",
                    "Role",
                    role.getId().toString(),
                    tenantId,
                    actorUserId == null ? "system" : actorUserId.toString());

            log.info(
                    "event=IAM_ROLE_CREATE_SUCCESS tenantId={} roleId={} name={}",
                    tenantId,
                    role.getId(),
                    role.getName());

            return toResponse(role, tenantId, hierarchy);
        } catch (TechMarketException e) {
            log.error(
                    "event=IAM_ROLE_CREATE_ERROR errorCode={} tenantId={} name={}",
                    e.getCode(),
                    tenantId,
                    request.name(),
                    e);
            throw e;
        } catch (Exception e) {
            log.error(
                    "event=IAM_ROLE_CREATE_ERROR tenantId={} name={}", tenantId, request.name(), e);
            throw new IamServiceException(ErrorCodes.IAM_ROLE_CREATE_FAILED, null, e);
        }
    }

    private Integer resolveHierarchyLevel(
            String tenantId, Integer requestedHierarchyLevel, Long parentRoleId) {
        if (requestedHierarchyLevel != null) {
            if (requestedHierarchyLevel < 0) {
                throw new IllegalArgumentException("hierarchyLevel must be non-negative");
            }
            if (parentRoleId != null) {
                RoleHierarchyView parent = findParentHierarchy(parentRoleId, tenantId);
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

        RoleHierarchyView parent = findParentHierarchy(parentRoleId, tenantId);
        return Math.max(IamConstants.DEFAULT_HIERARCHY_LEVEL, parent.getHierarchyLevel() + 1);
    }

    private RoleHierarchyView findParentHierarchy(Long parentRoleId, String tenantId) {
        RoleHierarchyView parent =
                roleHierarchyRepository.findHierarchyByRoleIds(Set.of(parentRoleId)).stream()
                        .findFirst()
                        .orElseThrow(
                                () -> new EntityNotFoundException("Role", parentRoleId.toString()));
        if (!tenantId.equals(parent.getTenantId())) {
            throw new IllegalArgumentException("parentRoleId must belong to same tenant");
        }
        return parent;
    }

    private RoleResponse toResponse(Role role, String tenantId, RoleHierarchyView hierarchy) {
        Integer hierarchyLevel =
                hierarchy == null
                        ? IamConstants.DEFAULT_HIERARCHY_LEVEL
                        : hierarchy.getHierarchyLevel();
        Long parentRoleId = hierarchy == null ? null : hierarchy.getParentRoleId();

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                tenantId,
                role.getPermissionIds(),
                hierarchyLevel,
                parentRoleId);
    }
}
