package com.techmarket.techmarket.users.api.admin.client.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(
        @NotNull(message = "cantidad is required")
                @Min(value = 1, message = "cantidad must be greater than zero")
                Integer cantidad) {}
