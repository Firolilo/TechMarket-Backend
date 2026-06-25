package com.techmarket.ai.bootstrap.config;

import com.techmarket.ai.application.port.out.VectorStorePort;
import com.techmarket.ai.infrastructure.vector.pgvector.PgVectorStoreAdapter;
import com.techmarket.ai.infrastructure.vector.stub.StubVectorStoreAdapter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the RAG {@link VectorStorePort}. Uses the real {@link PgVectorStoreAdapter} when a Spring
 * AI {@link VectorStore} exists (real embeddings + pgvector); otherwise falls back to the stub so
 * the AI service still boots and the RAG endpoints respond with empty results. Declaring the real
 * bean first makes the {@code @ConditionalOnMissingBean} fallback deterministic (same pattern as
 * {@code MarketplaceIndexConfig}).
 */
@Configuration
public class VectorStoreConfig {

    @Bean
    @ConditionalOnBean(VectorStore.class)
    public VectorStorePort pgVectorStorePort(VectorStore vectorStore) {
        return new PgVectorStoreAdapter(vectorStore);
    }

    @Bean
    @ConditionalOnMissingBean(VectorStorePort.class)
    public VectorStorePort stubVectorStorePort() {
        return new StubVectorStoreAdapter();
    }
}
