package com.techmarket.ai.infrastructure.llm.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.ai.application.port.out.StructuredLlmPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Structured LLM stub for the "test" profile only, so the test suite loads offline without calling
 * a real LLM. Returns an "empty" instance of the requested type.
 */
@Component
@Profile("test")
public class StubStructuredLlmAdapter implements StructuredLlmPort {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T generate(String systemPrompt, String userPrompt, Class<T> type) {
        try {
            return mapper.readValue("{}", type);
        } catch (Exception e) {
            return null;
        }
    }
}
