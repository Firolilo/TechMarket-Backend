package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSpecialistCertificationRequest(
        @NotBlank(message = "nombre is required") String nombre,
        String institucion,
        String fechaObtencion,
        String archivoUrl) {}
