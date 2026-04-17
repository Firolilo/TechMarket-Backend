package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.application.dto.CreateRoleRequest;
import com.techmarket.iamservice.application.dto.RoleResponse;
import com.techmarket.iamservice.application.service.RoleManagementService;
import com.techmarket.iamservice.application.service.TenantAuthorizationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Roles", description = "Role management operations")
@SecurityRequirement(name = "bearer-jwt")
public class RoleController {

    private final RoleManagementService roleManagementService;
    private final TenantAuthorizationService tenantAuthorizationService;

    public RoleController(
            RoleManagementService roleManagementService,
            TenantAuthorizationService tenantAuthorizationService) {
        this.roleManagementService = roleManagementService;
        this.tenantAuthorizationService = tenantAuthorizationService;
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForRead(tenantId, authentication);
        if (scopedTenantId == null) {
            return ResponseEntity.ok(roleManagementService.findAll());
        }
        return ResponseEntity.ok(roleManagementService.findAllByTenant(scopedTenantId));
    }

    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication,
            @Valid @RequestBody CreateRoleRequest request) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForWrite(
                        tenantId, request.tenantId(), authentication);
        RoleResponse roleResponse =
                roleManagementService.create(scopedTenantId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleResponse);
    }
}
