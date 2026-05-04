package com.techmarket.techmarket.marketplace.api.admin.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        String id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        List<String> imagenes,
        CompanySummaryResponse empresa,
        int stock) {}
