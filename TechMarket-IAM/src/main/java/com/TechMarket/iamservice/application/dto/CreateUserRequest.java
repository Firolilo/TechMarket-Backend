package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateUserRequest(
        @NotBlank(message = "username is required")
                @Size(min = 3, max = 100, message = "username must have 3-100 chars")
                String username,
        @NotBlank(message = "email is required") @Email(message = "email is invalid") String email,
        @NotBlank(message = "password is required")
                @Size(min = 8, max = 72, message = "password must have 8-72 chars")
                String password,
        String tenantId,
        Boolean active,
        @NotEmpty(message = "at least one role is required")
                @Size(max = 50, message = "maximum 50 roles allowed")
                Set<Long> roleIds,
        @NotBlank(message = "scopeType is required") String scopeType,
        @Size(max = 100, message = "maximum 100 branches allowed") Set<Long> branchIds) {}
