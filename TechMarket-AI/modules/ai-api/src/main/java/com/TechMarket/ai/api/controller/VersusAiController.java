package com.techmarket.ai.api.controller;

import com.techmarket.ai.application.dto.VersusAiDtos;
import com.techmarket.ai.application.dto.VersusVerdict;
import com.techmarket.ai.application.port.in.VersusAiUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Marketplace "Versus": AI verdict on which of two listings is the better deal. */
@RestController
@RequestMapping("/api/marketplace")
@Tag(name = "Versus AI", description = "AI deal comparator for two marketplace listings")
public class VersusAiController {

    private final VersusAiUseCase useCase;

    public VersusAiController(VersusAiUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/versus")
    @Operation(
            summary = "Compare two marketplace listings",
            description =
                    "Given two listings plus optional real-data context, returns which one is the"
                            + " better deal with per-product pros/cons and dimension scores.")
    public ResponseEntity<VersusVerdict> versus(@RequestBody VersusAiDtos.Compare request) {
        return ResponseEntity.ok(useCase.compare(request));
    }
}
