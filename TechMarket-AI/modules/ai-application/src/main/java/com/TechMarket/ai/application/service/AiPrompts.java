package com.techmarket.ai.application.service;

/** Small helpers for building LLM user prompts from optional business context. */
final class AiPrompts {

    private AiPrompts() {}

    /** Render optional context for a prompt, or a placeholder when none was provided. */
    static String context(Object context) {
        if (context == null) {
            return "(sin contexto adicional; usa supuestos razonables del dominio)";
        }
        String text = String.valueOf(context).trim();
        return text.isEmpty() ? "(sin contexto adicional)" : text;
    }
}
