package com.techmarket.techmarket.users.api.admin.client.response;

public record ClientAddressResponse(
        String id,
        String titulo,
        String pais,
        String ciudad,
        String direccion,
        String referencia,
        boolean esPredeterminada) {}
