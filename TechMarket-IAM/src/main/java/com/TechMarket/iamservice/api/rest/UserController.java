package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.api.exception.dto.ApiErrorResponse;
import com.techmarket.iamservice.application.dto.CreateUserRequest;
import com.techmarket.iamservice.application.dto.UpdateUserRequest;
import com.techmarket.iamservice.application.dto.UserResponse;
import com.techmarket.iamservice.application.service.TenantAuthorizationService;
import com.techmarket.iamservice.application.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam")
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

    @Operation(
            operationId = "getUsers",
            summary = "List users",
            description =
                    "Retrieves all users. If X-Tenant-Id header is provided, returns users scoped to that tenant.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Users retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array =
                                                @ArraySchema(
                                                        schema =
                                                                @Schema(
                                                                        implementation =
                                                                                UserResponse
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

    @Operation(
            operationId = "getUserById",
            summary = "Get user by ID",
            description = "Retrieves a single user by their identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User retrieved successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponse.class))),
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
                        description = "User not found",
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
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication,
            @Parameter(description = "User identifier", example = "1") @PathVariable Long id) {
        String scopedTenantId =
                tenantAuthorizationService.resolveTenantForRead(tenantId, authentication);
        if (scopedTenantId == null) {
            return ResponseEntity.ok(userManagementService.findById(id, authentication));
        }
        return ResponseEntity.ok(
                userManagementService.findById(scopedTenantId, id, authentication));
    }

    @Operation(
            operationId = "createUser",
            summary = "Create user",
            description = "Creates a new user with the specified roles and scope.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "User created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponse.class))),
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

    @Operation(
            operationId = "updateUser",
            summary = "Update user",
            description = "Updates an existing user's email, roles, scope, or active status.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponse.class))),
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
                        description = "User not found",
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
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            Authentication authentication,
            @Parameter(description = "User identifier", example = "1") @PathVariable Long id,
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
