package com.techmarket.ai.api.controller;

import com.techmarket.ai.application.dto.BusinessInsight;
import com.techmarket.ai.application.dto.EspecialistaAiDtos;
import com.techmarket.ai.application.port.in.EspecialistaAiUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Specialist (especialista) AI assistant endpoints. */
@RestController
@RequestMapping("/api/specialists/ai")
@Tag(name = "Specialist AI", description = "AI assistant for specialists")
public class EspecialistaAiController {

    private final EspecialistaAiUseCase useCase;

    public EspecialistaAiController(EspecialistaAiUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/insights")
    @Operation(summary = "AI dashboard insights for the specialist")
    public ResponseEntity<EspecialistaAiDtos.SpecialistInsights> insights() {
        return ResponseEntity.ok(useCase.insights());
    }

    @PostMapping("/query")
    @Operation(summary = "Ask the specialist AI assistant")
    public ResponseEntity<BusinessInsight> query(
            @RequestBody EspecialistaAiDtos.SpecialistQuery request) {
        return ResponseEntity.ok(useCase.query(request));
    }

    @PostMapping("/pricing-suggestion")
    @Operation(summary = "Suggest a pricing strategy for a service")
    public ResponseEntity<BusinessInsight> pricingSuggestion(
            @RequestBody(required = false) EspecialistaAiDtos.PricingSuggestionCommand request) {
        EspecialistaAiDtos.PricingSuggestionCommand command =
                request != null
                        ? request
                        : new EspecialistaAiDtos.PricingSuggestionCommand(null, null);
        return ResponseEntity.ok(useCase.pricingSuggestion(command));
    }

    @PostMapping("/improvement-plan")
    @Operation(summary = "Generate an improvement plan")
    public ResponseEntity<BusinessInsight> improvementPlan(
            @RequestBody(required = false) EspecialistaAiDtos.ImprovementCommand request) {
        EspecialistaAiDtos.ImprovementCommand command =
                request != null ? request : new EspecialistaAiDtos.ImprovementCommand(null);
        return ResponseEntity.ok(useCase.improvementPlan(command));
    }

    @PostMapping("/schedule-optimization")
    @Operation(summary = "Optimize availability and schedule")
    public ResponseEntity<BusinessInsight> scheduleOptimization() {
        return ResponseEntity.ok(useCase.scheduleOptimization());
    }
}
