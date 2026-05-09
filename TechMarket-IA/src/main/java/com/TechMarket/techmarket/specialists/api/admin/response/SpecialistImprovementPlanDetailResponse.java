package com.techmarket.techmarket.specialists.api.admin.response;

import java.util.List;

public record SpecialistImprovementPlanDetailResponse(
        String objetivo, List<String> acciones, String tiempoEstimado) {}
