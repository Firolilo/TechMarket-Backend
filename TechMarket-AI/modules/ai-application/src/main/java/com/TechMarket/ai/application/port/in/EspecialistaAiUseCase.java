package com.techmarket.ai.application.port.in;

import com.techmarket.ai.application.dto.BusinessInsight;
import com.techmarket.ai.application.dto.EspecialistaAiDtos;
import java.util.Map;

/** Specialist-facing AI assistant: dashboard insights, Q&A, pricing, plans and scheduling. */
public interface EspecialistaAiUseCase {

    EspecialistaAiDtos.SpecialistInsights insights(Map<String, Object> context);

    BusinessInsight query(EspecialistaAiDtos.SpecialistQuery command);

    BusinessInsight pricingSuggestion(EspecialistaAiDtos.PricingSuggestionCommand command);

    BusinessInsight improvementPlan(EspecialistaAiDtos.ImprovementCommand command);

    BusinessInsight scheduleOptimization(Map<String, Object> context);
}
