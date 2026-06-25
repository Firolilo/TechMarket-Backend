package com.techmarket.ai.application.port.in;

import com.techmarket.ai.application.dto.AmbassadorAiDtos;
import java.util.List;
import java.util.Map;

/** Ambassador-facing AI assistant: Q&A, insights, prospect scoring, follow-ups and plans. */
public interface AmbassadorAiUseCase {

    AmbassadorAiDtos.Answer query(AmbassadorAiDtos.Query command);

    List<AmbassadorAiDtos.Insight> insights(Map<String, Object> context);

    AmbassadorAiDtos.ProspectScore scoreProspect(AmbassadorAiDtos.ProspectScoreCommand command);

    AmbassadorAiDtos.FollowUpSuggestion followUp(AmbassadorAiDtos.FollowUpCommand command);

    AmbassadorAiDtos.ImprovementPlan improvementPlan(
            AmbassadorAiDtos.ImprovementPlanCommand command);
}
