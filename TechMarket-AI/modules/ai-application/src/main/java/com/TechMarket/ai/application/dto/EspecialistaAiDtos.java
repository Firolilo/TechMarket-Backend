package com.techmarket.ai.application.dto;

import java.util.List;
import java.util.Map;

/**
 * Request/response DTOs for the specialist (especialista) AI assistant. The query/pricing/plan/
 * schedule endpoints all return the shared {@link BusinessInsight}; {@code insights} returns the
 * dashboard shape from {@code SpecialistAiInsights} in the frontend. The optional {@code context}
 * carries the specialist's real data (reputation, services, activity) so the advice is grounded.
 */
public final class EspecialistaAiDtos {

    private EspecialistaAiDtos() {}

    public record SpecialistQuery(String consulta, Map<String, Object> context) {}

    public record PricingSuggestionCommand(
            String serviceId, String serviceName, Map<String, Object> context) {}

    public record ImprovementCommand(String focus, Map<String, Object> context) {}

    public record ScenarioPrompt(String title, String prompt, String impact) {}

    public record RadarBar(String label, int value) {}

    public record SpecialistInsights(
            List<String> recommendedQuestions,
            List<ScenarioPrompt> scenarioPrompts,
            List<RadarBar> radarBars) {}
}
