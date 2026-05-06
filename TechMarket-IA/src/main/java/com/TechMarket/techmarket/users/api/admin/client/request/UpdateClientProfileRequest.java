package com.techmarket.techmarket.users.api.admin.client.request;

import jakarta.validation.constraints.Size;

public record UpdateClientProfileRequest(
        @Size(max = 255, message = "nombre must have at most 255 chars") String nombre,
        @Size(max = 255, message = "apellido must have at most 255 chars") String apellido,
        @Size(max = 255, message = "telefono must have at most 255 chars") String telefono,
        @Size(max = 255, message = "avatar must have at most 255 chars") String avatar) {}
