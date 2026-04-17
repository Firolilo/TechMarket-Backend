package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.application.dto.CreateUserRequest;
import com.techmarket.iamservice.application.dto.UpdateUserRequest;
import com.techmarket.iamservice.application.dto.UserResponse;
import com.techmarket.iamservice.application.service.TenantAuthorizationService;
import com.techmarket.iamservice.application.service.UserManagementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Users", description = "User management operations")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final UserManagementService userManagementService;
    private final TenantAuthorizationService tenantAuthorizationService;

    public UserController(
            UserManagementService userManagementService,
            TenantAuthorizationService tenantAuthorizationService) {
        this.userManagementService = userManagementService;
        this.tenantAuthorizationService = tenantAuthorizationService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForRead(tenantId, authentication);
        if (scopedTenantId == null) {
            return ResponseEntity.ok(userManagementService.findAll(authentication));
        }
        return ResponseEntity.ok(
                userManagementService.findAllByTenant(scopedTenantId, authentication));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication,
            @PathVariable Long id) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForRead(tenantId, authentication);
        if (scopedTenantId == null) {
            return ResponseEntity.ok(userManagementService.findById(id, authentication));
        }
        return ResponseEntity.ok(
                userManagementService.findById(scopedTenantId, id, authentication));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication,
            @Valid @RequestBody CreateUserRequest request) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForWrite(
                        tenantId, request.tenantId(), authentication);
        UserResponse response =
                userManagementService.create(scopedTenantId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForRead(tenantId, authentication);
        if (scopedTenantId == null) {
            return ResponseEntity.ok(userManagementService.update(id, request, authentication));
        }
        return ResponseEntity.ok(
                userManagementService.update(scopedTenantId, id, request, authentication));
    }
}
