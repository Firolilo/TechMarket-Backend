package com.techmarket.iamservice.application.dto;

public record BranchResponse(Long id, String tenantId, String code, String name, boolean active) {}
