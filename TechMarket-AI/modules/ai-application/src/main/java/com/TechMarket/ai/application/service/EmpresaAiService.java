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
            Eres un asesor de negocio para una empresa que vende en TechMarket (marketplace y red \
            social de tecnología). Respondes en español, de forma concreta y accionable, basándote \
            en el contexto provisto; si falta información, asumes supuestos razonables. \
            'priority' y 'confidence' deben ser exactamente "Alta" o "Media". 'focusLabel' es una \
            sección sugerida (p. ej. "Publicaciones", "Chats", "Reseñas", "Analíticas") y \
            'focusHref' su ruta (p. ej. "/empresa/publicaciones").""";

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
