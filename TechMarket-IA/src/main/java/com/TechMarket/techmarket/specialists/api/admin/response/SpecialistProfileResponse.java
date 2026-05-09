package com.techmarket.techmarket.specialists.api.admin.response;

import java.math.BigDecimal;

public record SpecialistProfileResponse(
        String id, String nombre, String especialidad, String ubicacion, BigDecimal calificacion) {}
