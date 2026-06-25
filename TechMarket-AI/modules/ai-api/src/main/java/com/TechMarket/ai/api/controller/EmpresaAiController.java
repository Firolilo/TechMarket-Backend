package com.techmarket.ai.api.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.techmarket.ai.application.dto.BusinessInsight;
import com.techmarket.ai.application.dto.EmpresaAiDtos;
import com.techmarket.ai.application.port.in.EmpresaAiUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Company (empresa) AI assistant endpoints. */
@RestController
@RequestMapping("/api/empresa/ia")
@Tag(name = "Empresa AI", description = "AI assistant for companies")
public class EmpresaAiController {

    private final EmpresaAiUseCase useCase;

    public EmpresaAiController(EmpresaAiUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/consulta")
    @Operation(
            summary = "Company AI consultation",
            description =
                    "Turns a business question and optional context into an actionable insight.")
    public ResponseEntity<BusinessInsight> consultar(@RequestBody ConsultRequest request) {
        return ResponseEntity.ok(
                useCase.consult(new EmpresaAiDtos.Consult(request.question(), request.context())));
    }

    /**
     * API-layer request. Accepts the Spanish field names the frontend sends ({@code
     * consulta}/{@code pregunta}) as aliases so the user's question always reaches the model.
     */
    public record ConsultRequest(
            @JsonAlias({"consulta", "pregunta"}) String question, Map<String, Object> context) {}
}
