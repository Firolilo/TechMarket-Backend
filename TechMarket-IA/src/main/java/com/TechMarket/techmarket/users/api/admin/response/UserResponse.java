package com.techmarket.techmarket.users.api.admin.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}
