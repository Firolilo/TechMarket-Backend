package com.techmarket.iamservice.application.model;

public enum UserScopeType {
    GLOBAL,
    BRANCH;

    public static UserScopeType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("scopeType is required");
        }
        try {
            return UserScopeType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("scopeType must be GLOBAL or BRANCH");
        }
    }
}
