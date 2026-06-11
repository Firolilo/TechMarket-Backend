package com.techmarket.ai.application.dto;

import java.util.List;

/**
 * Request/response DTOs for the specialist (especialista) AI assistant. The query/pricing/plan/
 * schedule endpoints all return the shared {@link BusinessInsight}; {@code insights} returns the
 * dashboard shape from {@code SpecialistAiInsights} in the frontend.
 */
public final class EspecialistaAiDtos {

    private EspecialistaAiDtos() {}

    public record SpecialistQuery(String consulta) {}

    public record PricingSuggestionCommand(String serviceId, String serviceName) {}

    public record ImprovementCommand(String focus) {}

    public record ScenarioPrompt(String title, String prompt, String impact) {}

    public record RadarBar(String label, int value) {}

    public record SpecialistInsights(
            List<String> recommendedQuestions,
            List<ScenarioPrompt> scenarioPrompts,
            List<RadarBar> radarBars) {}
}
