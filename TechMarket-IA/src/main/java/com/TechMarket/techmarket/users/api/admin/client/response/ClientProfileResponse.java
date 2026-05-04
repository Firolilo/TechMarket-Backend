package com.techmarket.techmarket.users.api.admin.client.response;

import java.util.UUID;

public record ClientProfileResponse(
        UUID id,
        String email,
        String nombre,
        String apellido,
        String telefono,
        String avatar) {}
