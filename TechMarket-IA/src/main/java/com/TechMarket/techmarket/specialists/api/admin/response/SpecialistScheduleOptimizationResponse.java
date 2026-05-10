package com.techmarket.techmarket.specialists.api.admin.response;

import java.util.List;

public record SpecialistScheduleOptimizationResponse(
        String sugerencia, List<String> planSugerido) {}
