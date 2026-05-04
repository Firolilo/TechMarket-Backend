package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateUserRequest(
        @NotBlank(message = "{validation.user.username.required}")
                @Size(min = 3, max = 100, message = "{validation.user.username.size}")
                String username,
        @NotBlank(message = "{validation.user.email.required}")
                @Email(message = "{validation.user.email.invalid}")
                String email,
        @NotBlank(message = "{validation.user.password.required}")
                @Size(min = 8, max = 72, message = "{validation.user.password.size}")
                String password,
        String tenantId,
        Boolean active,
        @NotEmpty(message = "{validation.user.roles.required}")
                @Size(max = 50, message = "{validation.user.roles.size}")
                Set<Long> roleIds,
        @NotBlank(message = "{validation.user.scopeType.required}") String scopeType,
        @Size(max = 100, message = "{validation.user.branches.size}") Set<Long> branchIds) {}
