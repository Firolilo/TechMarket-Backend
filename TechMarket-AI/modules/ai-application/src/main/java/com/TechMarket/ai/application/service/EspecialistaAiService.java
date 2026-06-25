package com.techmarket.ai.application.service;

import com.techmarket.ai.application.dto.BusinessInsight;
import com.techmarket.ai.application.dto.EspecialistaAiDtos;
import com.techmarket.ai.application.port.in.EspecialistaAiUseCase;
import com.techmarket.ai.application.port.out.AuditPort;
import com.techmarket.ai.application.port.out.StructuredLlmPort;
import java.util.Map;

/** Specialist AI assistant backed by a structured LLM. */
public class EspecialistaAiService implements EspecialistaAiUseCase {

    private static final String PERSONA =
            "Eres un asesor para especialistas (freelancers de tecnología) que ofrecen servicios en"
                    + " TechMarket. Respondes en español, conciso y accionable, basándote en el"
                    + " contexto provisto.";

    private static final String INSIGHT_RULES =
            " Devuelve un insight accionable. 'priority' y 'confidence' deben ser exactamente"
                    + " \"Alta\" o \"Media\". 'focusLabel' es una sección sugerida (p. ej."
                    + " \"Servicios\", \"Disponibilidad\", \"Portafolio\", \"Solicitudes\") y"
                    + " 'focusHref' su ruta (p. ej. \"/especialista/servicios\").";

    private final StructuredLlmPort llm;
    private final AuditPort audit;

    public EspecialistaAiService(StructuredLlmPort llm, AuditPort audit) {
        this.llm = llm;
        this.audit = audit;
    }

    @Override
    public EspecialistaAiDtos.SpecialistInsights insights(Map<String, Object> context) {
        String system =
                PERSONA
                        + " Genera el panel de IA: 'recommendedQuestions' (3-5 preguntas útiles),"
                        + " 'scenarioPrompts' (2-3 con title/prompt/impact) y 'radarBars' (3-5"
                        + " métricas con label y value entre 0 y 100). Las métricas y preguntas"
                        + " deben reflejar el contexto real del especialista.";
        String user =
                withContext(
                        "Genera el panel inicial de asistencia para un especialista que quiere ganar"
                                + " más clientes y optimizar sus servicios.",
                        context);
        EspecialistaAiDtos.SpecialistInsights result =
                llm.generate(system, user, EspecialistaAiDtos.SpecialistInsights.class);
        audit.audit("especialista.ai.insights", Map.of());
        return result;
    }

    @Override
    public BusinessInsight query(EspecialistaAiDtos.SpecialistQuery command) {
        String user =
                withContext("Consulta del especialista: " + command.consulta(), command.context());
        BusinessInsight result = llm.generate(PERSONA + INSIGHT_RULES, user, BusinessInsight.class);
        audit.audit(
                "especialista.ai.query", Map.of("consulta", String.valueOf(command.consulta())));
        return result;
    }

    @Override
    public BusinessInsight pricingSuggestion(EspecialistaAiDtos.PricingSuggestionCommand command) {
        String system =
                PERSONA
                        + " Sugiere una estrategia de precios para el servicio indicado (analiza"
                        + " posicionamiento, valor y demanda)."
                        + INSIGHT_RULES;
        String user =
                withContext(
                        "Servicio: id=" + command.serviceId() + ", nombre=" + command.serviceName(),
                        command.context());
        BusinessInsight result = llm.generate(system, user, BusinessInsight.class);
        audit.audit(
                "especialista.ai.pricing-suggestion",
                Map.of("serviceName", String.valueOf(command.serviceName())));
        return result;
    }

    @Override
    public BusinessInsight improvementPlan(EspecialistaAiDtos.ImprovementCommand command) {
        String focus =
                command.focus() == null || command.focus().isBlank()
                        ? "reputación y captación de clientes"
                        : command.focus();
        String user =
                withContext("Crea un plan de mejora enfocado en: " + focus, command.context());
        BusinessInsight result =
                llm.generate(
                        PERSONA + " Estructura el plan en 'actionPlan'." + INSIGHT_RULES,
                        user,
                        BusinessInsight.class);
        audit.audit("especialista.ai.improvement-plan", Map.of("focus", focus));
        return result;
    }

    @Override
    public BusinessInsight scheduleOptimization(Map<String, Object> context) {
        String system =
                PERSONA
                        + " Recomienda cómo optimizar la disponibilidad y agenda para maximizar"
                        + " proyectos sin saturarse."
                        + INSIGHT_RULES;
        String user =
                withContext(
                        "Sugiere optimizaciones de agenda y disponibilidad para un especialista"
                                + " activo.",
                        context);
        BusinessInsight result = llm.generate(system, user, BusinessInsight.class);
        audit.audit("especialista.ai.schedule-optimization", Map.of());
        return result;
    }

    /** Appends the specialist's real data (from TechMarket-IA) to the user prompt when present. */
    private String withContext(String prompt, Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return prompt;
        }
        return prompt + "\n\nContexto real del especialista:\n" + AiPrompts.context(context);
    }
}
