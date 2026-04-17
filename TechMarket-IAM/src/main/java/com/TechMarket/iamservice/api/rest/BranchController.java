package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.application.dto.BranchResponse;
import com.techmarket.iamservice.application.dto.CreateBranchRequest;
import com.techmarket.iamservice.application.service.BranchManagementService;
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
@Tag(name = "Branches", description = "Branch management operations")
@SecurityRequirement(name = "bearer-jwt")
public class BranchController {

    private final BranchManagementService branchManagementService;
    private final TenantAuthorizationService tenantAuthorizationService;

    public BranchController(
            BranchManagementService branchManagementService,
            TenantAuthorizationService tenantAuthorizationService) {
        this.branchManagementService = branchManagementService;
        this.tenantAuthorizationService = tenantAuthorizationService;
    }

    @GetMapping("/branches")
    public ResponseEntity<List<BranchResponse>> getBranches(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForRead(tenantId, authentication);
        if (scopedTenantId == null) {
            throw new IllegalArgumentException("X-Tenant-Id header is required for branches");
        }
        return ResponseEntity.ok(branchManagementService.findAllByTenant(scopedTenantId));
    }

    @PostMapping("/branches")
    public ResponseEntity<BranchResponse> createBranch(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication,
            @Valid @RequestBody CreateBranchRequest request) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForWrite(
                        tenantId, request.tenantId(), authentication);
        BranchResponse response =
                branchManagementService.create(scopedTenantId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
