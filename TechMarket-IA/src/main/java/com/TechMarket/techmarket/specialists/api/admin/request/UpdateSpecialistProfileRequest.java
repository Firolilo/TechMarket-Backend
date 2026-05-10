package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.Size;

public record UpdateSpecialistProfileRequest(
        @Size(max = 255, message = "nombre must have at most 255 chars") String nombre,
        @Size(max = 255, message = "apellido must have at most 255 chars") String apellido,
        @Size(max = 255, message = "especialidad must have at most 255 chars") String especialidad,
        @Size(max = 255, message = "ubicacion must have at most 255 chars") String ubicacion) {}
