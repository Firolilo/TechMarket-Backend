package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record RespondReviewRequest(@NotBlank(message = "respuesta is required") String respuesta) {}
