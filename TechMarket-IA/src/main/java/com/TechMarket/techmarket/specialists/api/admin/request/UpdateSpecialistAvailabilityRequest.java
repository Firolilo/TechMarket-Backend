package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateSpecialistAvailabilityRequest(
        @Size(max = 32, message = "estado must have at most 32 chars") String estado,
        List<@Size(max = 32, message = "dias item must have at most 32 chars") String> dias,
        @Size(max = 16, message = "inicio must have at most 16 chars") String inicio,
        @Size(max = 16, message = "fin must have at most 16 chars") String fin,
        List<@Size(max = 32, message = "modalidad item must have at most 32 chars") String>
                modalidad,
        @Size(max = 255, message = "cobertura must have at most 255 chars") String cobertura) {}
