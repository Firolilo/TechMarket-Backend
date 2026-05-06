package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSpecialistPortfolioItemRequest(
        @NotBlank(message = "titulo is required")
                @Size(max = 255, message = "titulo must have at most 255 chars")
                String titulo,
        @Size(max = 255, message = "servicio must have at most 255 chars") String servicio,
        @Size(max = 255, message = "resultado must have at most 255 chars") String resultado,
        @Size(max = 32, message = "fecha must have at most 32 chars") String fecha) {}
