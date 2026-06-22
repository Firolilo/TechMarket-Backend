package com.techmarket.ai.api.controller;

import com.techmarket.ai.application.dto.AmbassadorAiDtos;
import com.techmarket.ai.application.port.in.AmbassadorAiUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Ambassador (embajador) AI assistant endpoints. */
@RestController
@RequestMapping("/api/ambassadors/ai")
@Tag(name = "Ambassador AI", description = "AI assistant for ambassadors")
public class AmbassadorAiController {

    private final AmbassadorAiUseCase useCase;

    public AmbassadorAiController(AmbassadorAiUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/query")
    @Operation(summary = "Ask the ambassador AI assistant")
    public ResponseEntity<AmbassadorAiDtos.Answer> query(
            @RequestBody AmbassadorAiDtos.Query request) {
        return ResponseEntity.ok(useCase.query(request));
    }

    @PostMapping("/insights")
    @Operation(summary = "Proactive AI insights for the ambassador")
    public ResponseEntity<List<AmbassadorAiDtos.Insight>> insights(
            @RequestBody(required = false) Map<String, Object> context) {
        return ResponseEntity.ok(useCase.insights(context));
    }

    @PostMapping("/prospect-score")
    @Operation(summary = "Score a prospect's potential")
    public ResponseEntity<AmbassadorAiDtos.ProspectScore> prospectScore(
            @RequestBody AmbassadorAiDtos.ProspectScoreCommand request) {
        return ResponseEntity.ok(useCase.scoreProspect(request));
    }

    @PostMapping("/follow-up-suggestion")
    @Operation(summary = "Suggest the next follow-up for a referral")
    public ResponseEntity<AmbassadorAiDtos.FollowUpSuggestion> followUp(
            @RequestBody AmbassadorAiDtos.FollowUpCommand request) {
        return ResponseEntity.ok(useCase.followUp(request));
    }

    @PostMapping("/improvement-plan")
    @Operation(summary = "Generate an improvement plan")
    public ResponseEntity<AmbassadorAiDtos.ImprovementPlan> improvementPlan(
            @RequestBody(required = false) AmbassadorAiDtos.ImprovementPlanCommand request) {
        AmbassadorAiDtos.ImprovementPlanCommand command =
                request != null ? request : new AmbassadorAiDtos.ImprovementPlanCommand(null);
        return ResponseEntity.ok(useCase.improvementPlan(command));
    }
}
