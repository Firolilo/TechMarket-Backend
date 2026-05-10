package com.techmarket.techmarket.users.api.admin.client.request;

import jakarta.validation.constraints.Size;

public record CancelOrderRequest(
        @Size(max = 255, message = "motivo must have at most 255 chars") String motivo) {}
