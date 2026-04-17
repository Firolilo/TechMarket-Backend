package com.techmarket.iamservice.application.orchestration;

import com.techmarket.core.iam.application.command.AssignPermissionsToRoleCommand;
import com.techmarket.core.iam.application.usecase.AssignPermissionsToRoleUseCase;
import com.techmarket.core.iam.domain.model.Role;
import com.techmarket.core.shared.exceptions.EntityNotFoundException;
import com.techmarket.core.shared.exceptions.TechMarketException;
import com.techmarket.iamservice.application.dto.AssignPermissionsToRoleRequest;
import com.techmarket.iamservice.application.dto.AssignPermissionsToRoleResponse;
import com.techmarket.iamservice.application.exception.IamServiceException;
import com.techmarket.iamservice.application.service.AuditTrailService;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantPermissionRepository;
import com.techmarket.iamservice.infrastructure.persistence.repository.TenantRoleRepository;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssignPermissionsToRoleOrchestrator {

    private static final Logger log =
            LoggerFactory.getLogger(AssignPermissionsToRoleOrchestrator.class);

    private final AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase;
    private final TenantRoleRepository tenantRoleRepository;
    private final TenantPermissionRepository tenantPermissionRepository;
    private final AuditTrailService auditTrailService;

    public AssignPermissionsToRoleOrchestrator(
            AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase,
            TenantRoleRepository tenantRoleRepository,
            TenantPermissionRepository tenantPermissionRepository,
            AuditTrailService auditTrailService) {
        this.assignPermissionsToRoleUseCase = assignPermissionsToRoleUseCase;
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantPermissionRepository = tenantPermissionRepository;
        this.auditTrailService = auditTrailService;
    }

    public AssignPermissionsToRoleResponse execute(AssignPermissionsToRoleRequest request) {
        log.info(
                "event=IAM_ASSIGN_PERMISSIONS_REQUEST_RECEIVED tenantId={} roleId={} permissionIdsCount={}",
                request.tenantId(),
                request.roleId(),
                request.permissionIds() != null ? request.permissionIds().size() : 0);

        try {
            tenantRoleRepository
                    .findByIdAndTenantId(request.roleId(), request.tenantId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Role", request.roleId().toString()));

            Set<Long> permissionIds =
                    request.permissionIds() == null
                            ? Set.of()
                            : new HashSet<>(request.permissionIds());
            if (!permissionIds.isEmpty()) {
                int found =
                        tenantPermissionRepository
                                .findByIdInAndTenantId(permissionIds, request.tenantId())
                                .size();
                if (found != permissionIds.size()) {
                    throw new IllegalArgumentException(
                            "One or more permissions do not exist for tenant");
                }
            }

            AssignPermissionsToRoleCommand command =
                    new AssignPermissionsToRoleCommand(request.roleId(), request.permissionIds());

            Role role = assignPermissionsToRoleUseCase.execute(command);

            log.info("event=IAM_ASSIGN_PERMISSIONS_SUCCESS roleId={}", role.getId());
            auditTrailService.record(
                    "ROLE_PERMISSIONS_UPDATED",
                    "Role",
                    role.getId().toString(),
                    request.tenantId(),
                    "system");
            return mapToResponse(role);
        } catch (TechMarketException e) {
            log.error(
                    "event=IAM_ASSIGN_PERMISSIONS_ERROR errorCode={} roleId={}",
                    e.getCode(),
                    request.roleId(),
                    e);
            throw e;
        } catch (Exception e) {
            log.error("event=IAM_ASSIGN_PERMISSIONS_ERROR roleId={}", request.roleId(), e);
            throw new IamServiceException("IAM_ASSIGN_PERMISSIONS_FAILED", null, e);
        }
    }

    private AssignPermissionsToRoleResponse mapToResponse(Role role) {
        return new AssignPermissionsToRoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getPermissionIds().stream().toList());
    }
}
