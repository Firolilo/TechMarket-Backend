package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdateUserRequest(
        @Email(message = "{validation.user.email.invalid}") String email,
        Boolean active,
        @Size(max = 50, message = "{validation.user.roles.size}") Set<Long> roleIds,
        String scopeType,
        @Size(max = 100, message = "{validation.user.branches.size}") Set<Long> branchIds) {}
