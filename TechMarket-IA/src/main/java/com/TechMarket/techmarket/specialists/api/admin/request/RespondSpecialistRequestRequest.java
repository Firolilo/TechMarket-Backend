package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record RespondSpecialistRequestRequest(
        @NotBlank(message = "accion is required") String accion) {}
