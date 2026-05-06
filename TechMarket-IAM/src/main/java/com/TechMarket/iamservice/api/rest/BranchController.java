package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.api.exception.ErrorCodes;
import com.techmarket.iamservice.api.exception.dto.ApiErrorResponse;
import com.techmarket.iamservice.application.dto.BranchResponse;
import com.techmarket.iamservice.application.dto.CreateBranchRequest;
import com.techmarket.iamservice.application.exception.AuthServiceException;
import com.techmarket.iamservice.application.service.BranchManagementService;
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

    @Operation(
            operationId = "getBranches",
            summary = "List branches",
            description =
                    "Retrieves all branches for the specified tenant. X-Tenant-Id header is required.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Branches retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array =
                                                @ArraySchema(
                                                        schema =
                                                                @Schema(
                                                                        implementation =
                                                                                BranchResponse
                                                                                        .class)))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Missing or invalid tenant",
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
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @GetMapping("/branches")
    public ResponseEntity<List<BranchResponse>> getBranches(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForRead(tenantId, authentication);
        if (scopedTenantId == null) {
            throw new AuthServiceException(
                    ErrorCodes.IAM_TENANT_REQUIRED,
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "X-Tenant-Id header is required for branches");
        }
        return ResponseEntity.ok(branchManagementService.findAllByTenant(scopedTenantId));
    }

    @Operation(
            operationId = "createBranch",
            summary = "Create branch",
            description = "Creates a new branch for the specified tenant.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Branch created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = BranchResponse.class))),
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
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
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
