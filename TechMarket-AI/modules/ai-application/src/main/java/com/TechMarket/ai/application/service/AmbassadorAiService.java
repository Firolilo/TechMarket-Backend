package com.techmarket.ai.application.service;

import com.techmarket.ai.application.dto.AmbassadorAiDtos;
import com.techmarket.ai.application.port.in.AmbassadorAiUseCase;
import com.techmarket.ai.application.port.out.AuditPort;
import com.techmarket.ai.application.port.out.StructuredLlmPort;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** Ambassador AI assistant backed by a structured LLM. */
public class AmbassadorAiService implements AmbassadorAiUseCase {

    private static final String PERSONA =
            "Eres un asesor para embajadores de TechMarket (programa de referidos que conecta"
                    + " negocios y especialistas con la plataforma y gana comisiones). Respondes en"
                    + " español, conciso y accionable, basándote en el contexto provisto.";

    private final StructuredLlmPort llm;
    private final AuditPort audit;

    public AmbassadorAiService(StructuredLlmPort llm, AuditPort audit) {
        this.llm = llm;
        this.audit = audit;
    }

    @Override
    public AmbassadorAiDtos.Answer query(AmbassadorAiDtos.Query command) {
        String system =
                PERSONA
                        + " 'confidence' es un número entre 0 y 1. 'sources' lista las señales o"
                        + " supuestos que respaldan la respuesta.";
        String user =
                "Consulta del embajador: "
                        + command.query()
                        + "\n\nContexto:\n"
                        + AiPrompts.context(command.context());
        AmbassadorAiDtos.Answer result = llm.generate(system, user, AmbassadorAiDtos.Answer.class);
        audit.audit("ambassador.ai.query", Map.of("query", String.valueOf(command.query())));
        return result;
    }

    @Override
    public List<AmbassadorAiDtos.Insight> insights(Map<String, Object> context) {
        String system =
                PERSONA
                        + " Genera entre 3 y 5 insights accionables. 'type' es una categoría corta"
                        + " (p. ej. \"oportunidad\", \"riesgo\", \"seguimiento\"). 'priority' es"
                        + " \"alta\", \"media\" o \"baja\". Los insights deben basarse en el contexto"
                        + " real del embajador.";
        String user =
                "Genera insights proactivos para que el embajador mejore sus referidos,"
                        + " conversiones y comisiones esta semana."
                        + (context == null || context.isEmpty()
                                ? ""
                                : "\n\nContexto real del embajador:\n"
                                        + AiPrompts.context(context));
        AmbassadorAiDtos.InsightDraftList drafts =
                llm.generate(system, user, AmbassadorAiDtos.InsightDraftList.class);
        List<AmbassadorAiDtos.InsightDraft> items =
                drafts == null || drafts.insights() == null ? List.of() : drafts.insights();
        String now = Instant.now().toString();
        List<AmbassadorAiDtos.Insight> result =
                items.stream()
                        .map(
                                d ->
                                        new AmbassadorAiDtos.Insight(
                                                UUID.randomUUID().toString(),
                                                d.type(),
                                                d.title(),
                                                d.description(),
                                                d.priority(),
                                                d.actionSuggestion(),
                                                now))
                        .collect(Collectors.toList());
        audit.audit("ambassador.ai.insights", Map.of("count", result.size()));
        return result;
    }

    @Override
    public AmbassadorAiDtos.ProspectScore scoreProspect(
            AmbassadorAiDtos.ProspectScoreCommand command) {
        String system =
                PERSONA
                        + " Evalúa el potencial de un prospecto (negocio) para sumarlo a TechMarket."
                        + " 'score' es un número entre 0 y 100. Cada 'factor' tiene 'impact'"
                        + " (\"Alto\", \"Medio\" o \"Bajo\") y 'weight' entre 0 y 1.";
        String user =
                "Evalúa este prospecto:\n"
                        + "- Negocio: "
                        + command.businessName()
                        + "\n- Categoría: "
                        + command.category()
                        + "\n- Ciudad: "
                        + command.city()
                        + "\n- Contacto: "
                        + AiPrompts.context(command.contactInfo());
        AmbassadorAiDtos.ProspectScore result =
                llm.generate(system, user, AmbassadorAiDtos.ProspectScore.class);
        audit.audit(
                "ambassador.ai.prospect-score",
                Map.of("businessName", String.valueOf(command.businessName())));
        return result;
    }

    @Override
    public AmbassadorAiDtos.FollowUpSuggestion followUp(AmbassadorAiDtos.FollowUpCommand command) {
        String system =
                PERSONA
                        + " Sugiere el siguiente seguimiento para un referido. 'channel' es"
                        + " \"WhatsApp\", \"Email\" o \"Llamada\". 'timing' es cuándo hacerlo."
                        + " 'templateMessage' es un mensaje listo para enviar, en español.";
        String user =
                "Referido (id): "
                        + command.referralId()
                        + "\n\nContexto del seguimiento:\n"
                        + AiPrompts.context(command.context());
        AmbassadorAiDtos.FollowUpSuggestion result =
                llm.generate(system, user, AmbassadorAiDtos.FollowUpSuggestion.class);
        audit.audit(
                "ambassador.ai.follow-up",
                Map.of("referralId", String.valueOf(command.referralId())));
        return result;
    }

    @Override
    public AmbassadorAiDtos.ImprovementPlan improvementPlan(
            AmbassadorAiDtos.ImprovementPlanCommand command) {
        String system =
                PERSONA
                        + " Crea un plan de mejora de 3 a 5 pasos. Cada paso tiene 'step' (número),"
                        + " 'action', 'expectedImpact' y 'deadline' (texto, p. ej. \"esta semana\").";
        String focus =
                command.focusArea() == null || command.focusArea().isBlank()
                        ? "rendimiento general como embajador"
                        : command.focusArea();
        String user = "Área de enfoque: " + focus;
        AmbassadorAiDtos.ImprovementPlan result =
                llm.generate(system, user, AmbassadorAiDtos.ImprovementPlan.class);
        audit.audit("ambassador.ai.improvement-plan", Map.of("focusArea", focus));
        return result;
    }
}
