package com.techmarket.techmarket.users.api.admin.client.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpsertReviewRequest(
        @NotNull(message = "calificacion is required")
                @Min(value = 1, message = "calificacion must be at least 1")
                @Max(value = 5, message = "calificacion must be at most 5")
                Integer calificacion,
        @Size(max = 255, message = "comentario must have at most 255 chars") String comentario) {}
