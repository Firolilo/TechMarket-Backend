package com.techmarket.core.iam.domain.service;

public interface PermissionEvaluatorService {

    boolean hasPermission(Long userId, String module, String resource, String action, String field);
}
