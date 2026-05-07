package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSpecialistChatMessageRequest(
        @NotBlank(message = "contenido is required") String contenido,
        String tipo) {}
