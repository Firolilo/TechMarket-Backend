package com.techmarket.ai.application.dto;

import java.util.Map;

/** Request DTOs for the company (empresa) AI assistant. */
public final class EmpresaAiDtos {

    private EmpresaAiDtos() {}

    /** Company business question plus optional structured context (metrics, recent activity, …). */
    public record Consult(String question, Map<String, Object> context) {}
}
