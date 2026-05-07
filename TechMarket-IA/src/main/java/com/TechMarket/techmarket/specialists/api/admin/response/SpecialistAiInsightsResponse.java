package com.techmarket.techmarket.specialists.api.admin.response;

import java.util.List;

public record SpecialistAiInsightsResponse(
        List<SpecialistAiInsightRadarResponse> radar, String recomendacion, String focoSugerido) {}
