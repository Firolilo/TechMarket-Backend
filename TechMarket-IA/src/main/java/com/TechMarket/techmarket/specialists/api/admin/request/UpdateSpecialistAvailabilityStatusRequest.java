package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSpecialistAvailabilityStatusRequest(
        @NotBlank(message = "estado is required")
                @Size(max = 32, message = "estado must have at most 32 chars")
                String estado,
        @Size(max = 255, message = "tiempoRespuesta must have at most 255 chars")
                String tiempoRespuesta) {}
