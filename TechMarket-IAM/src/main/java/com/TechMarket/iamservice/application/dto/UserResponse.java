package com.techmarket.iamservice.application.dto;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        boolean active,
        String tenantId,
        Set<Long> roleIds,
        Set<UserScopeResponse> scopes) {}
