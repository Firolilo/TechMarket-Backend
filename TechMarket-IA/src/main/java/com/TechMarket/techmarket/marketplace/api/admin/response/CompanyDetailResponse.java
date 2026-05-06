package com.techmarket.techmarket.marketplace.api.admin.response;

import java.time.LocalDate;

public record CompanyDetailResponse(
        String id,
        String nombre,
        String descripcion,
        LocalDate fechaRegistro,
        long ventasCompletadas) {}
