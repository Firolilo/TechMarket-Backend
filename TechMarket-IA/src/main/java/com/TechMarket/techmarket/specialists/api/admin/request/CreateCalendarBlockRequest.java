package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCalendarBlockRequest(
        @NotBlank(message = "fecha is required") String fecha,
        @NotBlank(message = "hora is required") String hora,
        String fin,
        String motivo) {}
