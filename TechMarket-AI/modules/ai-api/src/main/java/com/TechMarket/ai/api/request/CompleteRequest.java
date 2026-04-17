package com.techmarket.ai.api.request;

import jakarta.validation.constraints.NotBlank;

/** API request DTO for /complete. */
public record CompleteRequest(@NotBlank String prompt) {}
