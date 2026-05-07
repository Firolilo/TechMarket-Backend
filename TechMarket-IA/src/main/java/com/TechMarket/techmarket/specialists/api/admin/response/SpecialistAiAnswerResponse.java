package com.techmarket.techmarket.specialists.api.admin.response;

import java.util.List;

public record SpecialistAiAnswerResponse(String resumen, List<String> planAccion, String foco) {}
