package com.techmarket.techmarket.ambassadors.api.mobile.response;

import java.util.List;

public record DashboardStatsResponse(
        double economicImpact,
        double estimatedIncome,
        boolean isIncomeConfirmed,
        ActivityByTypeResponse activityByType,
        double variationPercent,
        String activityState,
        List<LevelImpactResponse> levelBreakdown) {

    public record ActivityByTypeResponse(
            int activeHardware, int activeSoftware, int activeServices) {}

    public record LevelImpactResponse(
            int level,
            double economicImpact,
            double percentageApplied,
            double incomeGenerated) {}
}
