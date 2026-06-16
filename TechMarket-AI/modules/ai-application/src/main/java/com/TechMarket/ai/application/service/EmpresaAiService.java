package com.techmarket.ai.application.service;

import com.techmarket.ai.application.dto.BusinessInsight;
import com.techmarket.ai.application.dto.EmpresaAiDtos;
import com.techmarket.ai.application.port.in.EmpresaAiUseCase;
import com.techmarket.ai.application.port.out.AuditPort;
import com.techmarket.ai.application.port.out.StructuredLlmPort;
import java.util.Map;

/** Company AI assistant backed by a structured LLM. */
public class EmpresaAiService implements EmpresaAiUseCase {

    private static final String SYSTEM =
            """
            Eres el consultor de negocio de TechMarket (marketplace y red social de tecnología) \
            para una empresa vendedora. Tu objetivo es darle recomendaciones concretas, accionables \
            y medibles para vender más, mejorar su reputación y crecer dentro de TechMarket.

            Reglas:
            - Responde SIEMPRE en español, con tono profesional y directo, y adapta la respuesta a \
            la pregunta concreta del usuario (no des respuestas genéricas).
            - Básate en el contexto provisto; si falta información, asume supuestos razonables y \
            menciónalos en 'dataPoints'.
            - Si la pregunta NO trata sobre la gestión del negocio en TechMarket (ventas, \
            publicaciones, chats, reseñas, analíticas, precios, clientes, marketing, reputación), \
            NO la respondas literalmente: en 'summary' indica con amabilidad que está fuera de tu \
            ámbito como consultor de negocio y reconduce hacia una consulta útil para su empresa; \
            deja 'actionPlan', 'dataPoints' y 'watchItems' vacíos o con un único elemento \
            orientativo, usa priority "Media" y confidence "Media".
            - Para preguntas válidas: 'dataPoints' = 3 a 5 observaciones o supuestos relevantes; \
            'actionPlan' = 3 pasos concretos y ejecutables; 'watchItems' = 2 a 3 métricas a \
            vigilar; 'advice' = el consejo principal; 'nextStep' = la primera acción a hacer hoy.
            - 'priority' y 'confidence' deben ser exactamente "Alta" o "Media".
            - 'focusLabel' es la sección de TechMarket sugerida y 'focusHref' su ruta. Usa solo: \
            "Publicaciones"=/empresa/publicaciones, "Chats"=/empresa/chat, "Reseñas"=/empresa/resenas, \
            "Analíticas"=/empresa/analiticas, "Perfil"=/empresa/perfil.""";

    private final StructuredLlmPort llm;
    private final AuditPort audit;

    public EmpresaAiService(StructuredLlmPort llm, AuditPort audit) {
        this.llm = llm;
        this.audit = audit;
    }

    @Override
    public BusinessInsight consult(EmpresaAiDtos.Consult command) {
        String user =
                "Pregunta del negocio: "
                        + command.question()
                        + "\n\nContexto del negocio:\n"
                        + AiPrompts.context(command.context());
        BusinessInsight result = llm.generate(SYSTEM, user, BusinessInsight.class);
        audit.audit("empresa.ai.consult", Map.of("question", String.valueOf(command.question())));
        return result;
    }
}
