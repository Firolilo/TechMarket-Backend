package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdateUserRequest(
        @Email(message = "email is invalid") String email,
        Boolean active,
        @Size(max = 50, message = "maximum 50 roles allowed") Set<Long> roleIds,
        String scopeType,
        @Size(max = 100, message = "maximum 100 branches allowed") Set<Long> branchIds) {}
