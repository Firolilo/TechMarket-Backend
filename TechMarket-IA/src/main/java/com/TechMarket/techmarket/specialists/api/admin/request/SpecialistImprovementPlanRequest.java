package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record SpecialistImprovementPlanRequest(
        @NotBlank(message = "area is required") String area) {}
