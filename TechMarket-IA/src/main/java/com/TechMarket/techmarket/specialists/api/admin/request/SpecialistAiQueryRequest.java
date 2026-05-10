package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record SpecialistAiQueryRequest(
        @NotBlank(message = "consulta is required") String consulta) {}
