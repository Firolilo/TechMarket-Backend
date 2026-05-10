package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSpecialistFileRequest(
        @NotBlank(message = "url is required") String url,
        @NotBlank(message = "nombre is required") String nombre,
        String tipo,
        String tamano) {}
