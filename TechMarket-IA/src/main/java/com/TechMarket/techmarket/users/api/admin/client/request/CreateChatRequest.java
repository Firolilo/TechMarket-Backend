package com.techmarket.techmarket.users.api.admin.client.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChatRequest(
        @NotBlank(message = "empresaId is required") String empresaId,
        @Size(max = 255, message = "asunto must have at most 255 chars") String asunto) {}
