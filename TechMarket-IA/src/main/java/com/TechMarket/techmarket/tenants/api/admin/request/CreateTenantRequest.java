package com.techmarket.techmarket.tenants.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTenantRequest(
        @NotBlank String businessName,
        @NotBlank String legalName,
        @NotBlank String taxId,
        @NotBlank String status) {}
