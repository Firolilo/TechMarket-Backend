package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBranchRequest(
        @NotBlank(message = "code is required")
                @Size(max = 100, message = "code must have at most 100 chars")
                String code,
        @NotBlank(message = "name is required")
                @Size(max = 100, message = "name must have at most 100 chars")
                String name,
        String tenantId,
        Boolean active) {}
