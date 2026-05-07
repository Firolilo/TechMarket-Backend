package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProjectStatusRequest(
        @NotBlank(message = "estado is required") String estado) {}
