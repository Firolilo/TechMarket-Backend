package com.techmarket.techmarket.users.api.admin.client.request;

import jakarta.validation.constraints.Size;

public record UpsertClientAddressRequest(
        @Size(max = 255, message = "titulo must have at most 255 chars") String titulo,
        @Size(max = 255, message = "pais must have at most 255 chars") String pais,
        @Size(max = 255, message = "ciudad must have at most 255 chars") String ciudad,
        @Size(max = 255, message = "direccion must have at most 255 chars") String direccion,
        @Size(max = 255, message = "referencia must have at most 255 chars") String referencia,
        Boolean esPredeterminada) {}
