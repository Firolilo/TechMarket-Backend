package com.techmarket.ai.application.dto;

import java.util.List;

/**
 * Actionable business insight produced by the AI. Field names match the frontend contracts shared
 * by the company AI page ({@code AiBusinessInsight}) and the specialist AI payload ({@code
 * SpecialistAiInsightPayload}).
 *
 * @param priority "Alta" | "Media"
 * @param confidence "Alta" | "Media"
 */
public record BusinessInsight(
        String summary,
        List<String> dataPoints,
        String advice,
        String nextStep,
        List<String> actionPlan,
        List<String> watchItems,
        String priority,
        String confidence,
        String focusLabel,
        String focusHref) {}
