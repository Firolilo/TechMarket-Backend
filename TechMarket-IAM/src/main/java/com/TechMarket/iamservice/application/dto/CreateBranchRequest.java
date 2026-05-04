package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBranchRequest(
        @NotBlank(message = "{validation.branch.code.required}")
                @Size(max = 100, message = "{validation.branch.code.size}")
                String code,
        @NotBlank(message = "{validation.branch.name.required}")
                @Size(max = 100, message = "{validation.branch.name.size}")
                String name,
        String tenantId,
        Boolean active) {}
