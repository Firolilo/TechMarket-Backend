package com.techmarket.techmarket.users.api.admin.client.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSpecialistChatRequest(
        @NotBlank(message = "especialistaId is required") String especialistaId,
        @Size(max = 255, message = "asunto must have at most 255 chars") String asunto) {}
