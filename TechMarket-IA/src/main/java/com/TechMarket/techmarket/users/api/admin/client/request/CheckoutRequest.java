package com.techmarket.techmarket.users.api.admin.client.request;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
        @NotBlank(message = "direccionEnvioId is required") String direccionEnvioId,
        @NotBlank(message = "metodoPago is required") String metodoPago) {}
