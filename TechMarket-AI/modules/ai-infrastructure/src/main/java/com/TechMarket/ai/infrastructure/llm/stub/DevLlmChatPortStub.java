package com.techmarket.ai.infrastructure.llm.stub;

import com.techmarket.ai.application.dto.ChatResultDto;
import com.techmarket.ai.application.port.out.LlmChatPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Stub LlmChatPort for the "test" profile only, so the test suite runs offline without calling a
 * real LLM. Every other profile uses the real {@link
 * com.techmarket.ai.infrastructure.llm.springai.SpringAiLlmChatAdapter} (Gemini).
 */
@Component
@Profile("test")
public class DevLlmChatPortStub implements LlmChatPort {

    private static final Logger log = LoggerFactory.getLogger(DevLlmChatPortStub.class);

    public DevLlmChatPortStub() {
        log.info("DevLlmChatPortStub initialized - using stub LLM responses in dev/test profile");
    }

    @Override
    public ChatResultDto chat(String prompt) {
        log.debug("DevLlmChatPortStub.chat() called with prompt: {}", prompt);
        return new ChatResultDto("[DEV-STUB] LLM no configurado. Respuesta simulada.", 0, 0);
    }
}
