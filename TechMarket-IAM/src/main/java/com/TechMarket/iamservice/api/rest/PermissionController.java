package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.api.exception.dto.ApiErrorResponse;
import com.techmarket.iamservice.application.dto.PermissionResponse;
import com.techmarket.iamservice.application.service.PermissionQueryService;
import com.techmarket.iamservice.application.service.TenantAuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam")
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

    @Operation(
            operationId = "getPermissions",
            summary = "List permissions",
            description =
                    "Retrieves all permissions. If X-Tenant-Id header is provided, returns permissions scoped to that tenant.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Permissions retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array =
                                                @ArraySchema(
                                                        schema =
                                                                @Schema(
                                                                        implementation =
                                                                                PermissionResponse
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
