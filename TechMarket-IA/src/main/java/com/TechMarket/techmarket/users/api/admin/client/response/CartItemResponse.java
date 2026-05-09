package com.techmarket.techmarket.users.api.admin.client.response;

import java.math.BigDecimal;

public record CartItemResponse(
        String id, String productoId, int cantidad, BigDecimal precioUnitario) {}
