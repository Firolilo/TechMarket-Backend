package com.techmarket.ai.infrastructure.llm.springai;

import com.techmarket.ai.application.dto.ChatResultDto;
import com.techmarket.ai.application.port.out.LlmChatPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * LLM chat adapter via Spring AI ChatClient. Returns answer + usage. Active on every profile except
 * "test" (the real LLM). By default the OpenAI client points at Google's OpenAI-compatible endpoint
 * (Gemini); Spring AI autoconfigures the ChatClient.Builder from the configured API key.
 */
@Component
@Profile("!test")
public class SpringAiLlmChatAdapter implements LlmChatPort {

    private final ChatClient chatClient;

    public SpringAiLlmChatAdapter(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public ChatResultDto chat(String prompt) {
        ChatResponse response;
        try {
            response = chatClient.prompt().user(prompt).call().chatResponse();
        } catch (RuntimeException ex) {
            throw LlmErrors.translate(ex);
        }
        String answer =
                response.getResult() != null && response.getResult().getOutput() != null
                        ? response.getResult().getOutput().getText()
                        : "";
        Usage usage =
                response.getMetadata() != null && response.getMetadata().getUsage() != null
                        ? response.getMetadata().getUsage()
                        : null;
        int promptTokens =
                usage != null ? (usage.getPromptTokens() != null ? usage.getPromptTokens() : 0) : 0;
        int completionTokens =
                usage != null
                        ? (usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0)
                        : 0;
        return new ChatResultDto(answer, promptTokens, completionTokens);
    }
}
