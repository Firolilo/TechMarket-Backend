package com.techmarket.iamservice.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 100, message = "nombre must have at most 100 chars") String nombre,
        @Size(max = 100, message = "apellido must have at most 100 chars") String apellido,
        @Size(max = 40, message = "telefono must have at most 40 chars") String telefono,
        @Size(max = 100, message = "ciudad must have at most 100 chars") String ciudad,
        @Size(max = 255, message = "descripcion must have at most 255 chars") String descripcion) {}
