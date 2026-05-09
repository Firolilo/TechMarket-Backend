package com.techmarket.iamservice.application.dto;

import java.time.Instant;

public record UserProfileResponse(
        String id,
        String email,
        String nombre,
        String apellido,
        String tipo,
        String telefono,
        String pais,
        String ciudad,
        String avatar,
        String estado,
        Instant fechaRegistro,
        Instant ultimaActualizacion) {}
