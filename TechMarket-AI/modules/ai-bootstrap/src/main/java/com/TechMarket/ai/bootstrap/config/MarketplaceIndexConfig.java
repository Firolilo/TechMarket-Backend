package com.techmarket.ai.bootstrap.config;

import com.techmarket.ai.application.port.out.MarketplaceIndexPort;
import com.techmarket.ai.infrastructure.vector.pgvector.MarketplaceVectorIndexAdapter;
import com.techmarket.ai.infrastructure.vector.stub.StubMarketplaceIndexAdapter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the marketplace semantic index. Uses the real {@link MarketplaceVectorIndexAdapter} when a
 * Spring AI {@link VectorStore} exists (real embeddings + pgvector/SimpleVectorStore); otherwise
 * falls back to the stub so the search endpoint still responds. Declaring the real bean first makes
 * the {@code @ConditionalOnMissingBean} fallback deterministic.
 */
@Configuration
public class MarketplaceIndexConfig {

    @Bean
    @ConditionalOnBean(VectorStore.class)
    public MarketplaceIndexPort marketplaceVectorIndex(VectorStore vectorStore) {
        return new MarketplaceVectorIndexAdapter(vectorStore);
    }

    @Bean
    @ConditionalOnMissingBean(MarketplaceIndexPort.class)
    public MarketplaceIndexPort marketplaceIndexStub() {
        return new StubMarketplaceIndexAdapter();
    }
}
