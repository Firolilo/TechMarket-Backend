package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.application.dto.PermissionResponse;
import com.techmarket.iamservice.application.service.PermissionQueryService;
import com.techmarket.iamservice.application.service.TenantAuthorizationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Permissions", description = "Permission management operations")
@SecurityRequirement(name = "bearer-jwt")
public class PermissionController {

    private final PermissionQueryService permissionQueryService;
    private final TenantAuthorizationService tenantAuthorizationService;

    public PermissionController(
            PermissionQueryService permissionQueryService,
            TenantAuthorizationService tenantAuthorizationService) {
        this.permissionQueryService = permissionQueryService;
        this.tenantAuthorizationService = tenantAuthorizationService;
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponse>> getPermissions(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForRead(tenantId, authentication);
        if (scopedTenantId == null) {
            return ResponseEntity.ok(permissionQueryService.findAll());
        }
        return ResponseEntity.ok(permissionQueryService.findAllByTenant(scopedTenantId));
    }
}
