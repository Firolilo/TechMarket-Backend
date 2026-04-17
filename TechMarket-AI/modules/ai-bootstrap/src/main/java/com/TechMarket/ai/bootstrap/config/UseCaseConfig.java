package com.techmarket.ai.bootstrap.config;

import com.techmarket.ai.application.port.in.CompletePromptUseCase;
import com.techmarket.ai.application.port.in.RagQaUseCase;
import com.techmarket.ai.application.port.out.AuditPort;
import com.techmarket.ai.application.port.out.LlmChatPort;
import com.techmarket.ai.application.port.out.LlmPort;
import com.techmarket.ai.application.port.out.TenantContextPort;
import com.techmarket.ai.application.port.out.VectorStorePort;
import com.techmarket.ai.application.service.CompletePromptService;
import com.techmarket.ai.application.service.RagQaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CompletePromptUseCase completePromptUseCase(LlmPort llmPort) {
        return new CompletePromptService(llmPort);
    }

    @Bean
    public RagQaUseCase ragQaUseCase(
            TenantContextPort tenantContext,
            VectorStorePort vectorStore,
            LlmChatPort llmChat,
            AuditPort audit) {
        return new RagQaService(tenantContext, vectorStore, llmChat, audit);
    }
}
