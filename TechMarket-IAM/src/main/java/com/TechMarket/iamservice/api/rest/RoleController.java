package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.api.exception.dto.ApiErrorResponse;
import com.techmarket.iamservice.application.dto.CreateRoleRequest;
import com.techmarket.iamservice.application.dto.RoleResponse;
import com.techmarket.iamservice.application.orchestration.CreateRoleOrchestrator;
import com.techmarket.iamservice.application.service.RoleManagementService;
import com.techmarket.iamservice.application.service.TenantAuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam")
@Tag(name = "Roles", description = "Role management operations")
@SecurityRequirement(name = "bearer-jwt")
public class RoleController {

    private final RoleManagementService roleManagementService;
    private final CreateRoleOrchestrator createRoleOrchestrator;
    private final TenantAuthorizationService tenantAuthorizationService;

    public RoleController(
            RoleManagementService roleManagementService,
            CreateRoleOrchestrator createRoleOrchestrator,
            TenantAuthorizationService tenantAuthorizationService) {
        this.roleManagementService = roleManagementService;
        this.createRoleOrchestrator = createRoleOrchestrator;
        this.tenantAuthorizationService = tenantAuthorizationService;
    }

    @Operation(
            operationId = "getRoles",
            summary = "List roles",
            description =
                    "Retrieves all roles. If X-Tenant-Id header is provided, returns roles scoped to that tenant.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Roles retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array =
                                                @ArraySchema(
                                                        schema =
                                                                @Schema(
                                                                        implementation =
                                                                                RoleResponse
                                                                                        .class)))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Not authenticated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Insufficient permissions",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
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

    @Operation(
            operationId = "createRole",
            summary = "Create role",
            description = "Creates a new role with optional hierarchy level and parent role.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Role created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = RoleResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Validation error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Not authenticated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Insufficient permissions",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Parent role not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication,
            @Valid @RequestBody CreateRoleRequest request) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForWrite(
                        tenantId, request.tenantId(), authentication);
        RoleResponse roleResponse =
                createRoleOrchestrator.execute(scopedTenantId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleResponse);
    }
}
