package com.techmarket.ai.infrastructure.llm.springai;

import com.techmarket.ai.application.port.out.StructuredLlmPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Structured LLM adapter via Spring AI ChatClient: maps the model's JSON response directly into the
 * requested record using {@code .entity(type)}. Active on every profile except "test" (uses the
 * configured provider, Gemini by default).
 */
@Component
@Profile("!test")
public class SpringAiStructuredLlmAdapter implements StructuredLlmPort {

    private final ChatClient chatClient;

    public SpringAiStructuredLlmAdapter(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public <T> T generate(String systemPrompt, String userPrompt, Class<T> type) {
        try {
            return chatClient.prompt().system(systemPrompt).user(userPrompt).call().entity(type);
        } catch (RuntimeException ex) {
            throw LlmErrors.translate(ex);
        }
    }
}
