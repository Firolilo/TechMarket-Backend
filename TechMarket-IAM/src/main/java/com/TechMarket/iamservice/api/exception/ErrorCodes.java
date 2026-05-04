package com.techmarket.iamservice.api.exception;

public final class ErrorCodes {

    private ErrorCodes() {}

    // Generic
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String UNEXPECTED_ERROR = "UNEXPECTED_ERROR";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";

    // Internal
    public static final String IAM_INTERNAL_ERROR = "IAM_INTERNAL_ERROR";

    // Auth
    public static final String IAM_INVALID_CREDENTIALS = "IAM_INVALID_CREDENTIALS";
    public static final String IAM_USER_INACTIVE = "IAM_USER_INACTIVE";
    public static final String IAM_INVALID_REFRESH_TOKEN = "IAM_INVALID_REFRESH_TOKEN";
    public static final String IAM_REFRESH_TOKEN_EXPIRED = "IAM_REFRESH_TOKEN_EXPIRED";
    public static final String IAM_INVALID_ACCESS_TOKEN = "IAM_INVALID_ACCESS_TOKEN";

    // User
    public static final String IAM_USER_NOT_FOUND = "IAM_USER_NOT_FOUND";
    public static final String IAM_TENANT_REQUIRED = "IAM_TENANT_REQUIRED";
    public static final String IAM_TENANT_INVALID = "IAM_TENANT_INVALID";

    // Role
    public static final String IAM_ROLE_NOT_FOUND = "IAM_ROLE_NOT_FOUND";
    public static final String IAM_ROLE_CREATE_FAILED = "IAM_ROLE_CREATE_FAILED";

    // Permission
    public static final String IAM_PERMISSION_NOT_FOUND = "IAM_PERMISSION_NOT_FOUND";
    public static final String IAM_ASSIGN_PERMISSIONS_FAILED = "IAM_ASSIGN_PERMISSIONS_FAILED";
}
