package com.techmarket.ai.application.port.out;

/**
 * Outbound port for structured LLM generation: the model returns an instance of the requested type
 * (JSON mapped to a Java record), not free text. Keeps the application free of any AI framework.
 */
public interface StructuredLlmPort {

    /**
     * Generate a structured result of {@code type} from a system prompt (role/instructions) and a
     * user prompt (task + business context).
     */
    <T> T generate(String systemPrompt, String userPrompt, Class<T> type);
}
