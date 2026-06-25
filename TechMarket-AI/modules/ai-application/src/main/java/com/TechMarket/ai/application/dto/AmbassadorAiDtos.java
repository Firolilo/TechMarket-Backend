package com.techmarket.ai.application.dto;

import java.util.List;
import java.util.Map;

/**
 * Request/response DTOs for the ambassador (embajador) AI assistant. Field names match the frontend
 * contracts in {@code lib/api/ambassador/types.ts}.
 */
public final class AmbassadorAiDtos {

    private AmbassadorAiDtos() {}

    // ---- query --------------------------------------------------------------
    public record Query(String query, Map<String, Object> context) {}

    public record Answer(String answer, double confidence, List<String> sources) {}

    // ---- insights -----------------------------------------------------------
    public record Insight(
            String id,
            String type,
            String title,
            String description,
            String priority,
            String actionSuggestion,
            String createdAt) {}

    /** What the LLM fills in; id and createdAt are set by the service, not the model. */
    public record InsightDraft(
            String type,
            String title,
            String description,
            String priority,
            String actionSuggestion) {}

    /** Wrapper so the LLM can return a list through a single typed object. */
    public record InsightDraftList(List<InsightDraft> insights) {}

    // ---- prospect score -----------------------------------------------------
    public record ProspectScoreCommand(
            String businessName, String category, String city, Map<String, String> contactInfo) {}

    public record ScoreFactor(String factor, String impact, double weight) {}

    public record ProspectScore(double score, List<ScoreFactor> factors, String recommendation) {}

    // ---- follow-up suggestion -----------------------------------------------
    public record FollowUpCommand(String referralId, String context) {}

    public record FollowUpSuggestion(
            String suggestion, String channel, String timing, String templateMessage) {}

    // ---- improvement plan ---------------------------------------------------
    public record ImprovementPlanCommand(String focusArea) {}

    public record PlanStep(int step, String action, String expectedImpact, String deadline) {}

    public record ImprovementPlan(List<PlanStep> plan, String overallGoal) {}
}
