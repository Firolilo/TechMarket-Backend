package com.techmarket.techmarket.users.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record User(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}
