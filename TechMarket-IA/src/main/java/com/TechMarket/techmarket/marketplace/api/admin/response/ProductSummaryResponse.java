package com.techmarket.techmarket.marketplace.api.admin.response;

import java.math.BigDecimal;

public record ProductSummaryResponse(
        String id, String nombre, BigDecimal precio, String imagenPrincipal, double calificacion) {}
